package de.hhu.bsinfo.neutrino.example.command;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.StringJoiner;

import de.hhu.bsinfo.infinileap.buffer.Buffer;
import de.hhu.bsinfo.infinileap.util.MemoryAlignment;
import de.hhu.bsinfo.infinileap.verbs.*;
import de.hhu.bsinfo.infinileap.verbs.QueuePair.AttributeFlag;
import jdk.incubator.foreign.MemoryAccess;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(
        name = "start",
        description = "Starts a new neutrino instance.%n",
        showDefaultValues = true,
        separator = " ")
public class Start implements Runnable {

    private static final byte DEFAULT_PORT = 1;
    private static final int DEFAULT_DEVICE = 0;

    private static final int DEFAULT_QUEUE_SIZE = 100;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final int DEFAULT_SERVER_PORT = 2998;

    private Context context;
    private Device device;
    private ProtectionDomain protectionDomain;
    private Buffer buffer;
    private MemoryRegion memoryRegion;
    private CompletionQueue completionQueue;
    private QueuePair queuePair;

    @CommandLine.Option(
            names = "--server",
            description = "Runs this instance in server mode.")
    private boolean isServer;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "The port the server will listen on.")
    private int portNumber = DEFAULT_SERVER_PORT;

    @CommandLine.Option(
            names = {"-b", "--buffer-size"},
            description = "Sets the memory regions buffer size.")
    private int bufferSize = DEFAULT_BUFFER_SIZE;

    @CommandLine.Option(
            names = "--connect",
            description = "The server to connect to.")
    private InetSocketAddress connection;


    @Override
    public void run() {
        if (!isServer && connection == null) {
            log.error("Please specify the server address");
            return;
        }

        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException {

        try (var devices = Verbs.queryDevices();
             var context = Verbs.openDevice(devices.get(DEFAULT_DEVICE))) {

            var deviceAttributes = context.queryDevice();
            var portAttributes = context.queryPort(DEFAULT_PORT);

            protectionDomain = context.allocateProtectionDomain();
            log.info("Allocated protection domain");

            buffer = protectionDomain.allocateMemory(bufferSize, MemoryAlignment.CACHE);
            log.info("Allocated buffer");

            completionQueue = context.createCompletionQueue(DEFAULT_QUEUE_SIZE);
            log.info("Created completion queue!");

            if (isServer) {
                startServer();
                send();
                poll();
            } else {
                startClient();
                receive();
                poll();
                log.info("{}", MemoryAccess.getLong(buffer));
            }
        }
    }

    private void startClient() throws IOException {
        var socket = new Socket(connection.getAddress(), connection.getPort());

        queuePair = createQueuePair(socket);

    }

    private void startServer() throws IOException {

        MemoryAccess.setLong(buffer, 0xC0FFEFEFEL);

        var serverSocket = new ServerSocket(portNumber);
        var socket = serverSocket.accept();

        queuePair = createQueuePair(socket);
    }

    private QueuePair createQueuePair(Socket socket) throws IOException {

        var initialAttributes = new QueuePair.InitialAttributes();
        initialAttributes.setReceiveCompletionQueue(completionQueue);
        initialAttributes.setSendCompletionQueue(completionQueue);
        initialAttributes.setQueuePairType(QueuePairType.RC);

        var capabilities = initialAttributes.getCapabilities();
        capabilities.setMaxSendWorkRequests(DEFAULT_QUEUE_SIZE);
        capabilities.setMaxReceiveWorkRequests(DEFAULT_QUEUE_SIZE);
        capabilities.setMaxReceiveScatterGatherElements(1);
        capabilities.setMaxSendScatterGatherElements(1);

        queuePair = protectionDomain.createQueuePair(initialAttributes);
        log.info("Created queue pair");

        var attributes = new QueuePair.Attributes();
        attributes.setCurrentQueuePairState(QueuePairState.INIT);
        attributes.setPrimaryPartitionKeyIndex((short) 0);
        attributes.setPortNumber((byte) 1);
        attributes.setQueuePairAccessFlags(AccessFlag.LOCAL_WRITE, AccessFlag.REMOTE_WRITE, AccessFlag.REMOTE_READ);

        queuePair.modify(attributes,
                AttributeFlag.STATE,
                AttributeFlag.PKEY_INDEX,
                AttributeFlag.PORT,
                AttributeFlag.ACCESS_FLAGS);

        log.info("Queue pair transitioned to INIT state!");

        var connectionInfo = exchangeInfo(socket, new ConnectionInfo(port.getLocalId(), queuePair.getQueuePairNumber()));

        attributes = new QueuePair.Attributes();
        attributes.setCurrentQueuePairState(QueuePairState.RTR);
        attributes.setPathMtu(Mtu.IBV_MTU_4096);
        attributes.setDestination(connectionInfo.getQueuePairNumber());
        attributes.setReceiveQueuePacketSerialNumber(0);
        attributes.setMaxDestinationAtomicResources((byte) 1);
        attributes.setMinReceiverNotReadyTimer((byte) 12);

        var addressVector = attributes.getPrimaryPathAddressVector()
        addressVector.setDestination(connectionInfo.getLocalId());
        addressVector.setServiceLevel((byte) 1);
        addressVector.setSourcePathBits((byte) 0);
        addressVector.setPortNumber((byte) 1);
        addressVector.setIsGlobal(false);

        queuePair.modify(attributes,
                AttributeFlag.STATE,
                AttributeFlag.PATH_MTU,
                AttributeFlag.DEST_QPN,
                AttributeFlag.RQ_PSN,
                AttributeFlag.AV,
                AttributeFlag.MAX_DEST_RD_ATOMIC,
                AttributeFlag.MIN_RNR_TIMER);

        log.info("Queue pair transitioned to RTR state");

        attributes = new QueuePair.Attributes();
        attributes.setCurrentQueuePairState(QueuePairState.RTS);
        attributes.setReceiveQueuePacketSerialNumber(0);
        attributes.setTimeout((byte) 14);
        attributes.setRetryCount((byte) 7);
        attributes.setReceiverNotReadyRetry((byte) 7);
        attributes.setMaxInitiatorAtomicReads((byte) 1);

        queuePair.modify(attributes,
                AttributeFlag.STATE,
                AttributeFlag.SQ_PSN,
                AttributeFlag.TIMEOUT,
                AttributeFlag.RETRY_CNT,
                AttributeFlag.RNR_RETRY,
                AttributeFlag.MAX_QP_RD_ATOMIC);

        log.info("Queue pair transitioned to RTS state");

        return queuePair;
    }

    private void send() {

        var scatterGatherElements = new NativeArray<>(ScatterGatherElement::new, ScatterGatherElement.class, 1);
        scatterGatherElements.apply(0, scatterGatherElement -> {
            scatterGatherElement.setAddress(memoryRegion.getAddress());
            scatterGatherElement.setLength((int) memoryRegion.getLength());
            scatterGatherElement.setLocalKey(memoryRegion.getLocalKey());
        });

        var sendWorkRequest = new SendWorkRequest(request -> {
            request.setOpCode(OpCode.SEND);
            request.setFlags(SendFlag.SIGNALED);
            request.setListHandle(scatterGatherElements.getHandle());
            request.setListLength(scatterGatherElements.getCapacity());
        });

        var list = new NativeLinkedList<>(SendWorkRequest.LINKER);
        list.add(sendWorkRequest);

        queuePair.postSend(list);
    }

    private void receive() {

        var scatterGatherElements = new NativeArray<>(ScatterGatherElement::new, ScatterGatherElement.class, 1);
        scatterGatherElements.apply(0, scatterGatherElement -> {
            scatterGatherElement.setAddress(memoryRegion.getAddress());
            scatterGatherElement.setLength((int) memoryRegion.getLength());
            scatterGatherElement.setLocalKey(memoryRegion.getLocalKey());
        });

        var receiveWorkRequest = new ReceiveWorkRequest(request -> {
            request.setListHandle(scatterGatherElements.getHandle());
            request.setListLength(scatterGatherElements.getCapacity());
        });

        var list = new NativeLinkedList<>(ReceiveWorkRequest.LINKER);
        list.add(receiveWorkRequest);

        queuePair.postReceive(list);
    }

    private void poll() {

        var completionArray = new WorkCompletionArray(DEFAULT_QUEUE_SIZE);

        while (completionArray.getLength() == 0) {
            completionQueue.poll(completionArray);
        }
    }

    private static ConnectionInfo exchangeInfo(Socket socket, ConnectionInfo localInfo) throws IOException {

        LOGGER.info("Sending connection info {}", localInfo);
        socket.getOutputStream().write(ByteBuffer.allocate(Short.BYTES + Integer.BYTES)
                .putShort(localInfo.getLocalId())
                .putInt(localInfo.getQueuePairNumber())
                .array());

        LOGGER.info("Waiting for remote connection info");
        var byteBuffer = ByteBuffer.wrap(socket.getInputStream().readNBytes(Short.BYTES + Integer.BYTES));

        var remoteInfo = new ConnectionInfo(byteBuffer.getShort(), byteBuffer.getInt());

        LOGGER.info("Received connection info {}", remoteInfo);

        return remoteInfo;
    }

    private static class ConnectionInfo {
        private final short localId;
        private final int queuePairNumber;

        public ConnectionInfo(short localId, int queuePairNumber) {
            this.localId = localId;
            this.queuePairNumber = queuePairNumber;
        }

        public short getLocalId() {
            return localId;
        }

        public int getQueuePairNumber() {
            return queuePairNumber;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", ConnectionInfo.class.getSimpleName() + "[", "]")
                    .add("localId=" + localId)
                    .add("queuePairNumber=" + queuePairNumber)
                    .toString();
        }
    }
}