package de.hhu.infinileap.engine.multiplex;

import lombok.extern.slf4j.Slf4j;
import org.agrona.CloseHelper;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.DynamicCompositeAgent;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.hints.ThreadHints;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class EventLoopGroup<T extends Agent> implements AutoCloseable, Iterable<EventLoop<T>> {

    /**
     * The runtime used by the current application.
     */
    private static final Runtime RUNTIME = Runtime.getRuntime();

    /**
     * The event loops contained within this group.
     */
    private final EventLoop<T>[] eventLoops;

    /**
     * The index used for retrieving the next event loop.
     */
    private final AtomicInteger index = new AtomicInteger();

    public EventLoopGroup(String name, Supplier<IdleStrategy> idleStrategySupplier) {
        this(name, RUNTIME.availableProcessors(), idleStrategySupplier);
    }

    @SuppressWarnings("unchecked")
    public EventLoopGroup(String name, int workerCount, Supplier<IdleStrategy> idleStrategySupplier) {
        if (workerCount <= 0) {
            throw new IllegalArgumentException("At least one worker has to be created");
        }

        log.debug("Using {} worker threads with idle strategy {}", workerCount, idleStrategySupplier.get());
        eventLoops = new EventLoop[workerCount];
        for (int i = 0; i < workerCount; i++) {
            eventLoops[i] = new EventLoop<>(name + "-" + i, idleStrategySupplier.get());
        }
    }

    public EventLoop<T> next() {
        return eventLoops[Math.floorMod(index.getAndIncrement(), eventLoops.length)];
    }

    public int size() {
        return eventLoops.length;
    }

    public void join() throws InterruptedException {
        for (var loop: eventLoops) {
            loop.join();
        }
    }

    public void start() {
        for (var loop: eventLoops) {
            loop.start();
            while (loop.status() != DynamicCompositeAgent.Status.ACTIVE) {
                LockSupport.parkNanos(Duration.ofMillis(100).toNanos());
            }
        }
    }

    public void waitOnStart() {
        for (var loop: eventLoops) {
            while (loop.status() != DynamicCompositeAgent.Status.ACTIVE) {
                ThreadHints.onSpinWait();
            }
        }
    }

    public EventLoop<T> first() {
        return eventLoops[0];
    }

    @Override
    public void close() {
        CloseHelper.quietCloseAll(eventLoops);
    }

    @Override
    public Iterator<EventLoop<T>> iterator() {
        return Arrays.stream(eventLoops).iterator();
    }
}
