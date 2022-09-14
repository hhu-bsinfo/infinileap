package de.hhu.bsinfo.infinileap.engine.agent;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.engine.agent.base.CommandableAgent;
import de.hhu.bsinfo.infinileap.engine.agent.base.EpollAgent;
import de.hhu.bsinfo.infinileap.engine.agent.command.AcceptCommand;
import de.hhu.bsinfo.infinileap.engine.agent.command.AgentCommand;
import de.hhu.bsinfo.infinileap.engine.agent.command.ListenCommand;
import de.hhu.bsinfo.infinileap.engine.agent.util.AgentOperations;
import de.hhu.bsinfo.infinileap.engine.agent.util.WakeReason;
import de.hhu.bsinfo.infinileap.engine.multiplex.EventLoopGroup;
import de.hhu.bsinfo.infinileap.engine.pipeline.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Supplier;

@Slf4j
public class AcceptorAgent extends CommandableAgent {

    /**
     * The worker instance this agent uses for making progress.
     */
    private final Worker worker;

    /**
     * The listener instance this agent uses for processing incoming connection requests.
     */
    private Listener listener;

    /**
     * The group of event loops this agent dispatches its connection requests to.
     */
    private final EventLoopGroup<CommandableAgent> workerGroup;

    public AcceptorAgent(Worker worker, EventLoopGroup<CommandableAgent> workerGroup) {
        this.worker = worker;
        this.workerGroup = workerGroup;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            add(worker, WakeReason.PROGRESS, EventType.EPOLLIN, EventType.EPOLLOUT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onSelect(SelectionKey<WakeReason> selectionKey) throws IOException {
        if (selectionKey.attachment() == WakeReason.PROGRESS) {
            AgentOperations.progressWorker(worker);
        }
    }

    @Override
    protected void onCommand(AgentCommand<?> command) {
        if (command.type() == AgentCommand.Type.LISTEN) {
            startListener((ListenCommand) command);
        }
    }

    private void startListener(ListenCommand command) {
        if (listener != null) {
            log.warn("Listener already active");
            return;
        }

        var listenerParameters = new ListenerParameters()
                .setConnectionHandler(this.connectionHandler)
                .setListenAddress(command.getListenAddress());

        try {
            this.listener = worker.createListener(listenerParameters);
        } catch (ControlException e) {
            throw new RuntimeException(e);
        }

        log.info("Listening on {}", command.getListenAddress());
        command.complete(null);
    }

    private final ConnectionHandler connectionHandler = new ConnectionHandler() {
        @Override
        protected void onConnection(ConnectionRequest request) {
            try {
                log.info("Received new connection request from {}", request.getClientAddress());
            } catch (ControlException e) {
                throw new RuntimeException(e);
            }

            // Instruct next agent to accept and manage the incoming connection
            var nextAgent = workerGroup.next().getAgent();
            nextAgent.pushCommand(new AcceptCommand(request));
        }
    };

    @Override
    public String roleName() {
        return "acceptor";
    }
}
