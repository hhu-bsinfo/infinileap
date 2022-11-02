package de.hhu.bsinfo.infinileap.engine.event.loop;

import lombok.extern.slf4j.Slf4j;
import org.agrona.hints.ThreadHints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
public class EventLoopGroup<T extends AbstractEventLoop> implements Iterable<T> {

    /**
     * The runtime used by the current application.
     */
    private static final Runtime RUNTIME = Runtime.getRuntime();

    /**
     * The event loops contained within this group.
     */
    private final List<T> eventLoops = new ArrayList<>();

    /**
     * The index used for retrieving the next event loop.
     */
    private final AtomicInteger index = new AtomicInteger();

    public void populate(int count, EventLoopFactory<T> eventLoopFactory) {
        for (int i = 0; i < count; i++) {
            eventLoops.add(eventLoopFactory.newInstance());
        }
    }

    public T next() {
        return eventLoops.get(Math.floorMod(index.getAndIncrement(), eventLoops.size()));
    }

    public void join() throws InterruptedException {
        for (var loop: eventLoops) {
            loop.join();
        }
    }

    public void start(ThreadFactory threadFactory) {
        for (var loop: eventLoops) {
            loop.start(threadFactory);
        }
    }

    public void waitOnStart() {
        for (var loop: eventLoops) {
            while (loop.status() != EventLoopStatus.RUNNING) {
                ThreadHints.onSpinWait();
            }
        }
    }

    public T first() {
        return eventLoops.get(0);
    }



    @Override
    public Iterator<T> iterator() {
        return eventLoops.iterator();
    }
}
