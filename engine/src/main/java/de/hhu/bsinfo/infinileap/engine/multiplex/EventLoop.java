package de.hhu.bsinfo.infinileap.engine.multiplex;

import lombok.extern.slf4j.Slf4j;
import org.agrona.CloseHelper;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.DynamicCompositeAgent;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.hints.ThreadHints;

import java.util.concurrent.ThreadFactory;

@Slf4j
public final class EventLoop<T extends Agent> implements AutoCloseable {

    /**
     * The composite agent performing work for this event loop.
     */
    private final DynamicCompositeAgent compositeAgent;

    /**
     * The runner used by this event loop.
     */
    private final AgentRunner runner;

    /**
     * The thread factory used by agent runners.
     */
    private final ThreadFactory threadFactory;

    /**
     * The thread on which this event loop is run.
     */
    private Thread thread;

    private T agent;

    public EventLoop(String name, IdleStrategy idleStrategy, ThreadFactory threadFactory) {
        compositeAgent = new DynamicCompositeAgent(name);
        runner = new AgentRunner(idleStrategy, EventLoop::errorHandler, null, compositeAgent);
        this.threadFactory = threadFactory;
    }

    public void add(T agent) {
        while (!compositeAgent.tryAdd(agent)) {
            ThreadHints.onSpinWait();
        }

        this.agent = agent;
    }

    public void join(int timeout) throws InterruptedException {
        thread.join(timeout);
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    public T getAgent() {
        return agent;
    }

    public DynamicCompositeAgent.Status status() {
        return compositeAgent.status();
    }

    void start() {
        thread = AgentRunner.startOnThread(runner, threadFactory);
    }

    @Override
    public void close() {
        CloseHelper.quietClose(runner);
    }

    private static void errorHandler(Throwable throwable) {
        log.error("Encountered unexpected error", throwable);
    }
}