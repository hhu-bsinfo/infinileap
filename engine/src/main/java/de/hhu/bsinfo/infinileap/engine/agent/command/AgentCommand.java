package de.hhu.bsinfo.infinileap.engine.agent.command;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AgentCommand<T> {

    private final CountDownLatch completion = new CountDownLatch(1);

    private final AtomicReference<T> result = new AtomicReference<>();

    public enum Type {
        CONNECT, LISTEN, ACCEPT
    }

    public abstract Type type();

    public void complete(T result) {
        this.result.set(result);
        this.completion.countDown();
    }

    public T await() throws InterruptedException {
        this.completion.await();
        return result.get();
    }

    public T await(Duration timeout) throws InterruptedException {
        this.completion.await(timeout.toNanos(), TimeUnit.NANOSECONDS);
        return result.get();
    }
}
