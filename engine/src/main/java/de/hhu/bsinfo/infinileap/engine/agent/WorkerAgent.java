package de.hhu.bsinfo.infinileap.engine.agent;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.engine.agent.base.CommandableAgent;
import de.hhu.bsinfo.infinileap.engine.agent.base.EpollAgent;
import de.hhu.bsinfo.infinileap.engine.agent.command.AcceptCommand;
import de.hhu.bsinfo.infinileap.engine.agent.command.AgentCommand;
import de.hhu.bsinfo.infinileap.engine.agent.command.ConnectCommand;
import de.hhu.bsinfo.infinileap.engine.agent.util.AgentOperations;
import de.hhu.bsinfo.infinileap.engine.agent.util.WakeReason;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.message.MessageDispatcher;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import jdk.incubator.foreign.MemoryAddress;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WorkerAgent extends CommandableAgent {

    /**
     * The maximum number of endpoints this agent supports.
     */
    private static final int MAX_ENDPOINT_COUNT = 4096;

    /**
     * The worker instance associated with this agent.
     */
    private final Worker worker;

    private final RingBuffer requestBuffer = new RingBuffer(128 * MemoryAlignment.PAGE.value());

    private int channelCounter = 0;

    /**
     * All channels associated with this agent instance.
     */
    private final Channel[] channels = new Channel[MAX_ENDPOINT_COUNT];

    /**
     * Maps from endpoint memory address to the corresponding channel.
     */
    private final Map<MemoryAddress, Channel> channelMap = new HashMap<>();

    private final MessageDispatcher messageDispatcher;

    public WorkerAgent(Worker worker, Object serviceInstance) {
        this.worker = worker;
        messageDispatcher = MessageDispatcher.forServiceInstance(serviceInstance, channelMap::get);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register message dispatcher with this agent's worker instance

        try {
            messageDispatcher.registerOn(worker);
        } catch (ControlException e) {
            throw new RuntimeException(e);
        }

        // Add file descriptors to epoll instance in order to wake up
        // the event loop once an event occures.

        try {
            add(worker, WakeReason.PROGRESS, EventType.EPOLLIN, EventType.EPOLLOUT);
            add(requestBuffer, WakeReason.DATA, EventType.EPOLLIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onSelect(SelectionKey<WakeReason> selectionKey) throws IOException {
        switch (selectionKey.attachment()) {
            case DATA -> processRequests();
            case PROGRESS -> AgentOperations.progressWorker(worker);
        }
    }

    @Override
    protected void onCommand(AgentCommand<?> command) {
        switch (command.type()) {
            case CONNECT -> processConnect((ConnectCommand) command);
            case ACCEPT -> processAccept((AcceptCommand) command);
        }
    }

    private void processAccept(AcceptCommand command) {
        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(command.getConnectionRequest());

        var channel = newChannel();

        try {
            var endpoint = worker.createEndpoint(endpointParameters);
            registerChannel(endpoint, channel);
            log.info("Accepted connection from {}", command.getConnectionRequest().getClientAddress());
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

    private void processRequests() {

    }


    private int nextChannelId() {
        return channelCounter++;
    }

    private Channel newChannel() {
        return new Channel(nextChannelId(), requestBuffer);
    }

    private void registerChannel(Endpoint endpoint, Channel channel) {
        var identifier = channel.identifier();
        if (this.channels[identifier] != null) {
            log.warn("Overriding channel with id {}", identifier);
        }

        this.channels[identifier] = channel;
        this.channelMap.put(endpoint.address(), channel);
    }

//    public void send(Identifier identifier, MemorySegment header, MemorySegment data, Callback<Void> callback) {
//        // Register callback
//        var requestIdentifier = REQUEST_COUNTER.incrementAndGet();
//        requestParameters.setUserData(requestIdentifier);
//        requestMap.put(requestIdentifier, callback);
//
//        // Send message
//        var request = endpoint.sendActive(
//                identifier,
//                header,
//                data,
//                requestParameters
//        );
//
//        if (Status.is(request, Status.OK)) {
//            requestMap.remove(requestIdentifier).onComplete();
//        }
//    }

    private final RingBuffer.MessageHandler requestHandler = (msgTypeId, buffer, index, length) -> {

    };

    public Worker getWorker() {
        return worker;
    }

    @Override
    public String roleName() {
        return "worker";
    }
}
