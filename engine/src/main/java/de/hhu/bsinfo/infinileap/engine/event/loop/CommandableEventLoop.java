package de.hhu.bsinfo.infinileap.engine.event.loop;

import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.engine.event.command.EventLoopCommand;
import de.hhu.bsinfo.infinileap.engine.event.util.CommandQueue;
import de.hhu.bsinfo.infinileap.engine.event.util.WakeReason;
import org.agrona.hints.ThreadHints;

import java.io.IOException;

public abstract class CommandableEventLoop extends EpollEventLoop<WakeReason> {

    private static final int COMMAND_QUEUE_SIZE = 4096;

    private final CommandQueue commands = new CommandQueue(COMMAND_QUEUE_SIZE);

    @Override
    public void onStart() throws Exception {
        super.onStart();

        // Add command queue file descriptor to epoll interest list.

        try {
            add(commands, WakeReason.COMMAND, EventType.EPOLLIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void process(SelectionKey<WakeReason> selectionKey) throws IOException {
        if (selectionKey.attachment() == WakeReason.COMMAND) {
            commands.disarm();
            commands.drain(this::onCommand);
        } else {
            onSelect(selectionKey);
        }
    }

    public void pushCommand(EventLoopCommand<?> command) {
        while(!commands.offer(command)) {
            ThreadHints.onSpinWait();
        }

        commands.fire();
    }

    protected abstract void onSelect(SelectionKey<WakeReason> selectionKey) throws IOException;

    protected abstract void onCommand(EventLoopCommand<?> command);
}
