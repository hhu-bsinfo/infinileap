package de.hhu.bsinfo.infinileap.engine.event.loop.spin;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import de.hhu.bsinfo.infinileap.common.multiplex.EventFileDescriptor;
import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.engine.buffer.BufferPool;
import de.hhu.bsinfo.infinileap.engine.buffer.DynamicBufferPool;
import de.hhu.bsinfo.infinileap.engine.buffer.PooledBuffer;
import de.hhu.bsinfo.infinileap.engine.buffer.StaticBufferPool;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.event.command.AcceptCommand;
import de.hhu.bsinfo.infinileap.engine.event.command.ConnectCommand;
import de.hhu.bsinfo.infinileap.engine.event.command.EventLoopCommand;
import de.hhu.bsinfo.infinileap.engine.event.loop.CommandableEventLoop;
import de.hhu.bsinfo.infinileap.engine.event.loop.EventLoopContext;
import de.hhu.bsinfo.infinileap.engine.event.message.SendActiveMessage;
import de.hhu.bsinfo.infinileap.engine.event.util.EventLoopOperations;
import de.hhu.bsinfo.infinileap.engine.event.util.WakeReason;
import de.hhu.bsinfo.infinileap.engine.message.CallManager;
import de.hhu.bsinfo.infinileap.engine.util.DebouncingLogger;
import lombok.extern.slf4j.Slf4j;
import org.agrona.collections.Long2ObjectHashMap;

import java.io.IOException;

import static org.openucx.Communication.ucp_request_free;

@Slf4j
public class SpinningWorkerEventLoop extends SpinningCommandableEventLoop {

    private static final int REQUEST_LIMIT = Integer.MAX_VALUE;

    /**
     * The maximum number of endpoints this agent supports.
     */
    private static final int MAX_ENDPOINT_COUNT = 4096;

    /**
     * The worker instance associated with this agent.
     */
    private final Worker worker;

    private final RingBuffer requestBuffer;

    private int channelCounter = 0;

    /**
     * All channels associated with this agent instance.
     */
    private final Channel[] channels = new Channel[MAX_ENDPOINT_COUNT];

    /**
     * All endpoints associated with this agent instance.
     */
    private final Endpoint[] endpoints = new Endpoint[MAX_ENDPOINT_COUNT];

    /**
     * Maps from endpoint memory address to the corresponding channel.
     */
    private final Long2ObjectHashMap<Channel> channelMap = new Long2ObjectHashMap<>();

    private final BufferPool sharedPool;

    private CallManager callManager;

    private EventLoopContext context;

    private final Object serviceInstance;

    private final EventFileDescriptor workerNotifier;

    private final DebouncingLogger debouncingLogger = new DebouncingLogger(1000);

    public SpinningWorkerEventLoop(Worker worker, StaticBufferPool sharedPool, Object serviceInstance) {
        this.worker = worker;
        this.sharedPool = sharedPool;
        this.serviceInstance = serviceInstance;
        this.requestBuffer = new RingBuffer(32 * MemoryAlignment.PAGE.value());

        try {
            this.workerNotifier = EventFileDescriptor.create(EventFileDescriptor.OpenMode.NONBLOCK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStart() throws Exception {
        super.onStart();

        // Create context
        this.context = EventLoopContext.builder()
                .privatePool(new DynamicBufferPool(1, 4096))
                .sharedPool(sharedPool)
                .thread(Thread.currentThread())
                .worker(worker)
                .loop(this)
                .loopNotifier(workerNotifier)
                .build();

        // Create handlers for worker callbacks
        callManager = CallManager.forServiceInstance(serviceInstance, channelMap::get);

        try {
            // Register message dispatcher with this loop's worker instance
            callManager.registerOn(worker);
            log.debug("Registered message dispatcher");
        } catch (ControlException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected LoopStatus doWork() throws Exception {

        // Process commands
        var loopStatus = super.doWork();

        // Progress UCX Worker
        if (EventLoopOperations.progressWorker(worker) == WorkerProgress.ACTIVE) {
            loopStatus = LoopStatus.ACTIVE;
        }

        // Process outgoing messages
        var bytesRead = requestBuffer.read(requestHandler, REQUEST_LIMIT);
        requestBuffer.commitRead(bytesRead);
        if (bytesRead != 0) {
            loopStatus = LoopStatus.ACTIVE;
        }

        return loopStatus;
    }



    @Override
    protected void onCommand(EventLoopCommand<?> command) {
        switch (command.type()) {
            case CONNECT -> processConnect((ConnectCommand) command);
            case ACCEPT  -> processAccept((AcceptCommand) command);
        }
    }

    private void processAccept(AcceptCommand command) {
        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(command.getConnectionRequest());

        var channel = newChannel();

        try {
            var endpoint = worker.createEndpoint(endpointParameters);
            registerChannel(endpoint, channel);
        } catch (ControlException e) {
            throw new RuntimeException(e);
        }

        command.complete(channel);
    }

    private void processConnect(ConnectCommand command) {
        var endpointParameters = new EndpointParameters()
                .setRemoteAddress(command.getRemoteAddress())
                .enableClientIdentifier();

        try {

            // Create the channel and assign it an unique identifier
            var endpoint = worker.createEndpoint(endpointParameters);
            var channel = newChannel();
            registerChannel(endpoint, channel);
            command.complete(channel);

        } catch (ControlException e) {
            throw new RuntimeException(e);
        }
    }


    private int nextChannelId() {
        return channelCounter++;
    }

    private Channel newChannel() {
        return new Channel(nextChannelId(), requestBuffer, context);
    }

    private void registerChannel(Endpoint endpoint, Channel channel) {
        var identifier = channel.identifier();
        if (this.channels[identifier] != null) {
            log.warn("Overriding channel with id {}", identifier);
        }

        this.channels[identifier] = channel;
        this.endpoints[identifier] = endpoint;
        this.channelMap.put(endpoint.address().toRawLongValue(), channel);
        log.info("Registered endpoint 0x{}: {}", Long.toHexString(endpoint.address().toRawLongValue()), channelMap.get(endpoint.address().toRawLongValue()));
    }

    private final SendCallback sendCallback = (request, status, data) -> {
        ucp_request_free(request);
        var userBuffer = getBuffer((int) data.toRawLongValue());
        var callback = userBuffer.getCallback();
        userBuffer.release();
        callback.onComplete();
    };

    private final RequestParameters activeMessageParameters = new RequestParameters()
            .setDataType(DataType.CONTIGUOUS_8_BIT)
            .setSendCallback(sendCallback)
            .setFlags(RequestParameters.Flag.ACTIVE_MESSAGE_REPLY);

    private PooledBuffer getBuffer(int identifier) {
        return context.getBuffer(identifier);
    }

    private final RingBuffer.MessageHandler requestHandler = (msgTypeId, buffer, index, length) -> {
        var slice = buffer.asSlice(index, length);
        var channelId = SendActiveMessage.getChannelId(slice);
        var bufferId = SendActiveMessage.getBufferId(slice);
        var messageId = SendActiveMessage.getMessageId(slice);
        var userLength = SendActiveMessage.getLength(slice);

        sendActive(channelId, bufferId, messageId, userLength);
    };

    public final void sendActive(int channelId, int bufferId, int messageId, int length) {
        var userBuffer = getBuffer(bufferId);

        activeMessageParameters.setUserData(bufferId);
        var endpoint = endpoints[channelId];
        var request = endpoint.sendActive(
                Identifier.of(messageId),
                userBuffer.segment().asSlice(0, length),
                null,
                activeMessageParameters
        );

        if (Status.is(request, Status.OK)) {
            var callback = userBuffer.getCallback();
            userBuffer.release();
            callback.onComplete();
        }
    }


    public Worker getWorker() {
        return worker;
    }
}
