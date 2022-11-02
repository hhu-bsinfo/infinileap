package de.hhu.bsinfo.infinileap.engine.event.loop;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.engine.event.command.AcceptCommand;
import de.hhu.bsinfo.infinileap.engine.event.command.EventLoopCommand;
import de.hhu.bsinfo.infinileap.engine.event.command.ListenCommand;
import de.hhu.bsinfo.infinileap.engine.event.util.EventLoopOperations;
import de.hhu.bsinfo.infinileap.engine.event.util.WakeReason;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class AcceptorEventLoop extends CommandableEventLoop {

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
    private final EventLoopGroup<CommandableEventLoop> workerGroup;

    public AcceptorEventLoop(Worker worker, EventLoopGroup<CommandableEventLoop> workerGroup) {
        this.worker = worker;
        this.workerGroup = workerGroup;
    }

    @Override
    public void onStart() throws Exception {
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
            EventLoopOperations.progressWorker(worker);
        }
    }

    @Override
    protected void onCommand(EventLoopCommand<?> command) {
        if (command.type() == EventLoopCommand.Type.LISTEN) {
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
            var workerLoop = workerGroup.next();
            workerLoop.pushCommand(new AcceptCommand(request));
        }
    };
}
