package de.hhu.bsinfo.infinileap.example.benchmark.connection;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.util.BenchmarkBarrier;
import de.hhu.bsinfo.infinileap.example.util.Constants;
import de.hhu.bsinfo.infinileap.example.util.Requests;
import de.hhu.bsinfo.infinileap.util.CloseException;
import de.hhu.bsinfo.infinileap.util.ResourcePool;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class BenchmarkServer {


    /**
     * The network address this server listens on.
     */
    private final InetSocketAddress listenAddress;

    /**
     * This node's context.
     */
    private Context context;

    /**
     * This node's worker instance.
     */
    private Worker worker;

    /**
     * The endpoint used for communication with the other side.
     */
    private Endpoint endpoint;

    /**
     * The listener used for accepting new connections.
     */
    private Listener listener;

    /**
     * The local memory region used for remote memory access operations.
     */
    private MemoryRegion sendRegion;

    /**
     * The local buffer used for remote memory access operations.
     */
    private MemorySegment sendBuffer;

    /**
     * The local memory region used for remote memory access operations.
     */
    private MemoryRegion receiveRegion;

    /**
     * The local buffer used for remote memory access operations.
     */
    private MemorySegment receiveBuffer;

    /**
     * The remote address used for memory access operations.
     */
    private MemoryAddress remoteAddress;

    /**
     * Key used for accessing remote memory.
     */
    private RemoteKey remoteKey;

    /**
     * Handles cleanup of resources created during the demo.
     */
    private final ResourcePool resources = new ResourcePool();

    private BenchmarkServer(InetSocketAddress listenAddress) {
        this.listenAddress = listenAddress;
    }

    public void start() {
        var isRunning = true;
        var counter = 1;
        while (isRunning) {
            log.info("Running loop {}", counter);
            try (resources) {
                initialize();
                executeBenchmark();
                BenchmarkBarrier.await(worker);
                counter++;
            } catch (ControlException e) {
                log.error("Native operation failed", e);
            } catch (CloseException e) {
                log.error("Closing resource failed", e);
            }
        }
    }

    private void initialize() throws ControlException, CloseException {
        var connectionResources = ConnectionResources.create();
        context = resources.push(connectionResources::context);
        worker = resources.push(connectionResources::worker);
        var connectionRequest = accept();

        log.trace("Accepted new client connection");
        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(connectionRequest);

        endpoint = worker.createEndpoint(endpointParameters);
        resources.push(endpoint);
    }

    private ConnectionRequest accept() throws ControlException, CloseException {
        try (var pool = new ResourcePool()) {
            var connectionRequest = new AtomicReference<ConnectionRequest>();
            var listenerParams = pool.push(() -> new ListenerParameters()
                    .setListenAddress(listenAddress)
                    .setConnectionHandler(connectionRequest::set));

            log.trace("Listening for new connection requests on {}", listenAddress);
            listener = worker.createListener(listenerParams);
            resources.push(listener);

            Requests.await(worker, connectionRequest);
            return connectionRequest.get();
        }
    }

    private void executeBenchmark() throws ControlException, CloseException {
        var opCode = Requests.receiveOpCode(worker);
        try (var details = Requests.receiveDetails(worker)) {

            // Initialize send and receive buffers
            initBuffers(details.getBufferSize());
            BenchmarkBarrier.await(worker);

            // Execute operation
            switch (opCode) {
                case RUN_READ_LATENCY -> readLatency(details);
                case RUN_WRITE_LATENCY -> writeLatency(details);
                case RUN_SEND_LATENCY -> sendLatency(details);
                case RUN_PINGPONG_LATENCY -> pingPongLatency(details);
                case RUN_ATOMIC_LATENCY -> atomicLatency(details);
                default -> throw new IllegalStateException("Received unexpected op code " + opCode.name());
            }
        }
    }

    private void readLatency(BenchmarkDetails details) throws ControlException {

    }

    private byte writeCounter = 1;

    private void writeLatency(BenchmarkDetails details) throws ControlException {
        var isReceiving = true;
        while (isReceiving) {
            if (waitForValue(worker, receiveBuffer, writeCounter)) {
                isReceiving = false;
            }

            MemoryAccess.setByte(sendBuffer, writeCounter++);
            Requests.blockingPut(worker, endpoint, sendBuffer, remoteAddress, remoteKey);
        }

        writeCounter = 1;
    }

    private void sendLatency(BenchmarkDetails details) throws ControlException {
        while (hasNextMessage(receiveBuffer)) {
            Requests.poll(worker, worker.receiveTagged(receiveBuffer, Constants.TAG_BENCHMARK_MESSAGE));
        }
    }

    private void pingPongLatency(BenchmarkDetails details) throws ControlException {
        while (hasNextMessage(receiveBuffer)) {
            Requests.poll(worker, worker.receiveTagged(receiveBuffer, Constants.TAG_BENCHMARK_MESSAGE));
            Requests.poll(worker, endpoint.sendTagged(sendBuffer, Constants.TAG_BENCHMARK_MESSAGE));
        }
    }

    private void atomicLatency(BenchmarkDetails details) throws ControlException {

    }

    private void initBuffers(long size) throws ControlException {
        sendRegion = context.allocateMemory(size);
        receiveRegion = context.allocateMemory(size);
        resources.push(sendRegion);
        resources.push(receiveRegion);

        sendBuffer = sendRegion.segment();
        receiveBuffer = receiveRegion.segment();
        resources.push(sendBuffer);
        resources.push(receiveBuffer);

        // Exchange memory region information
        Requests.sendDescriptor(worker, endpoint, receiveRegion.descriptor());
        try (var descriptor = Requests.receiveDescriptor(worker)) {
            remoteKey = endpoint.unpack(descriptor);
            remoteAddress = descriptor.remoteAddress();
            resources.push(remoteKey);
        }
    }

    private static boolean hasNextMessage(MemorySegment segment) {
        return MemoryAccess.getByte(segment) != Constants.LAST_MESSAGE;
    }

    private static boolean waitForValue(Worker worker, MemorySegment segment, byte value) {
        while (MemoryAccess.getByte(segment) != value) {
            worker.progress();
            if (MemoryAccess.getByteAtOffset(segment, 1) == Constants.LAST_MESSAGE) {
                return true;
            }
        }

        return false;
    }

    public static BenchmarkServer create(InetSocketAddress listenAddress) throws ControlException {
        return new BenchmarkServer(listenAddress);
    }
}
