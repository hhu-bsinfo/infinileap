package de.hhu.bsinfo.infinileap.engine.event.loop.spin;

import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.engine.event.command.EventLoopCommand;
import de.hhu.bsinfo.infinileap.engine.event.loop.EpollEventLoop;
import de.hhu.bsinfo.infinileap.engine.event.util.CommandQueue;
import de.hhu.bsinfo.infinileap.engine.event.util.WakeReason;
import org.agrona.concurrent.ManyToOneConcurrentArrayQueue;
import org.agrona.hints.ThreadHints;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

public abstract class SpinningCommandableEventLoop extends PhasedEventLoop {

    private static final int COMMAND_QUEUE_SIZE = 4096;

    private final ManyToOneConcurrentArrayQueue<EventLoopCommand<?>> commands;

    private final Consumer<EventLoopCommand<?>> callback = this::onCommand;

    protected SpinningCommandableEventLoop() {
        commands = new ManyToOneConcurrentArrayQueue<>(COMMAND_QUEUE_SIZE);
    }

    @Override
    protected LoopStatus doWork() throws Exception {
        return commands.drain(callback) == 0 ? LoopStatus.IDLE : LoopStatus.ACTIVE;
    }

    public void pushCommand(EventLoopCommand<?> command) {
        while(!commands.offer(command)) {
            ThreadHints.onSpinWait();
        }
    }

    protected abstract void onCommand(EventLoopCommand<?> command);
}
