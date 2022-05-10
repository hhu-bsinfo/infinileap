package de.hhu.bsinfo.infinileap.engine.agent;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.engine.agent.command.AcceptCommand;
import de.hhu.bsinfo.infinileap.engine.agent.command.AgentCommand;
import de.hhu.bsinfo.infinileap.engine.agent.command.ConnectCommand;
import de.hhu.bsinfo.infinileap.engine.agent.command.ListenCommand;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import lombok.extern.slf4j.Slf4j;
import org.agrona.hints.ThreadHints;

import java.io.IOException;

@Slf4j
public class ConnectionAgent extends EpollAgent<ConnectionAgent.WakeReason> {

    private static final int COMMAND_QUEUE_SIZE = 4096;

    private static final int MAX_ENDPOINT_COUNT = 4096;

    public enum WakeReason {
        COMMAND, PROGRESS, DATA
    }

    private final Worker worker;

    private final RingBuffer requestBuffer = new RingBuffer(128 * MemoryAlignment.PAGE.value());

    private final CommandQueue commands = new CommandQueue(COMMAND_QUEUE_SIZE);

    private final BufferPool bufferPool;

    /**
     * The listener instance this agent uses for listening to new connections. May be null if this agent
     * does not accept connections.
     */
    private Listener listener;
    private ListenerParameters listenerParameters;

    private int channelCounter = 0;

    // All channels associated with this agent instance.
    private final Channel[] channels = new Channel[MAX_ENDPOINT_COUNT];

    public ConnectionAgent(Worker worker, BufferPool bufferPool) {
        this.worker = worker;
        this.bufferPool = bufferPool;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add file descriptors to epoll instance in order to wake up
        // the event loop once an event occures.

        try {
            add(worker, WakeReason.PROGRESS, EventType.EPOLLIN, EventType.EPOLLOUT);
            add(requestBuffer, WakeReason.DATA, EventType.EPOLLIN);
            add(commands, WakeReason.COMMAND, EventType.EPOLLIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void process(SelectionKey<WakeReason> selectionKey) throws IOException {
        switch (selectionKey.attachment()) {
            case DATA -> processRequests();
            case PROGRESS -> processWorker();
            case COMMAND -> {
                commands.disarm();
                commands.drain(this::processCommand);
            }
        }
    }

    private void processCommand(AgentCommand<?> command) {
        switch (command.type()) {
            case LISTEN -> processListen((ListenCommand) command);
            case CONNECT -> processConnect((ConnectCommand) command);
            case ACCEPT -> processAccept((AcceptCommand) command);
        }
    }

    private void processAccept(AcceptCommand command) {
        var endpointParameters = new EndpointParameters()
                .setConnectionRequest(command.getConnectionRequest());

        try {
            worker.createEndpoint(endpointParameters);
        } catch (ControlException e) {
            throw new RuntimeException(e);
        }
    }

    private void processListen(ListenCommand command) {
        if (listener != null) {
            log.warn("Listener already active");
            return;
        }


        this.listenerParameters = new ListenerParameters()
                .setConnectionHandler(this.connectionHandler)
                .setListenAddress(command.getListenAddress());

        try {
            this.listener = worker.createListener(listenerParameters);
        } catch (ControlException e) {
            throw new RuntimeException(e);
        }

        log.info("Listening on {}", command.getListenAddress());
    }

    private void processConnect(ConnectCommand command) {
        var endpointParameters = new EndpointParameters()
                .setRemoteAddress(command.getRemoteAddress())
                .enableClientIdentifier();

        try {

            // Create the channel and assign it an unique identifier
            var endpoint = worker.createEndpoint(endpointParameters);
            var channel = new Channel(channelCounter++, endpoint, bufferPool);

            // Register channel and complete the command
            channels[channel.getChannelIdentifier()] = channel;
            command.complete(channel);

        } catch (ControlException e) {
            throw new RuntimeException(e);
        }
    }

    private void processRequests() {

    }

    private void processWorker() {
        while (worker.progress() == WorkerProgress.ACTIVE) {
            // Busy Spin
        }
    }

    public void pushCommand(AgentCommand command) {
        while(!commands.offer(command)) {
            ThreadHints.onSpinWait();
        }

        commands.fire();
    }

    private final RingBuffer.MessageHandler requestHandler = (msgTypeId, buffer, index, length) -> {

    };

    private final ConnectionHandler connectionHandler = new ConnectionHandler() {
        @Override
        protected void onConnection(ConnectionRequest request) {
            try {
                log.debug("Received new connection request from {}", request.getClientAddress());
            } catch (ControlException e) {
                // ignored
            }
        }
    };

    public Worker getWorker() {
        return worker;
    }

    @Override
    public String roleName() {
        return null;
    }
}
