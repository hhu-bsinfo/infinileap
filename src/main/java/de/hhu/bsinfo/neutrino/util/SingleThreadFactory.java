package de.hhu.bsinfo.neutrino.util;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public class SingleThreadFactory implements ThreadFactory {

    private final ThreadGroup threadGroup;
    private final AtomicBoolean isThreadCreated = new AtomicBoolean(false);
    private final String name;

    public SingleThreadFactory(final String name) {
        SecurityManager s = System.getSecurityManager();
        threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.name = name;
    }

    public Thread newThread(@NotNull Runnable r) {
        if (isThreadCreated.getAndSet(true)) {
            throw new IllegalStateException("Thread was already created");
        }

        Thread t = new Thread(threadGroup, r, name, 0);

        if (t.isDaemon()) {
            t.setDaemon(false);
        }

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        return t;
    }
}
