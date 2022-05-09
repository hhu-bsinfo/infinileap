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

    /**
     * The listener instance this agent uses for listening to new connections. May be null if this agent
     * does not accept connections.
     */
    private Listener listener;
    private ListenerParameters listenerParameters;

    private int endpointCounter = 0;
    private final Endpoint[] endpoints = new Endpoint[MAX_ENDPOINT_COUNT];

    public ConnectionAgent(Worker worker) {
        super();
        this.worker = worker;
    }

    @Override
    public void onStart() {
        super.onStart();

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

    private void processCommand(AgentCommand command) {
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
            endpoints[endpointCounter++] = worker.createEndpoint(endpointParameters);
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
