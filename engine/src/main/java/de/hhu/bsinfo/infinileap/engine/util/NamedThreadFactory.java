package de.hhu.bsinfo.infinileap.engine.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final String prefix;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, String.format("%s-%02d", prefix, threadNumber.getAndIncrement()));
    }
}
