package de.hhu.bsinfo.neutrino.api.queue;

import de.hhu.bsinfo.neutrino.scheduler.Schedulers;
import de.hhu.bsinfo.neutrino.verbs.CompletionChannel;
import de.hhu.bsinfo.neutrino.verbs.CompletionQueue;
import de.hhu.bsinfo.neutrino.verbs.CompletionQueue.WorkCompletionArray;
import de.hhu.bsinfo.neutrino.verbs.WorkCompletion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class QueueProcessor extends Thread implements Closeable {

    public enum Mode {
        QUEUE, CHANNEL
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueProcessor.class);

    private final CompletionQueue[] completionQueues;
    private final CompletionChannel completionChannel;

    private final WorkCompletionArray completionArray = new WorkCompletionArray(100);
    private final CompletionHandler handler;

    private volatile boolean isRunning = true;

    private static final String THREAD_NAME = "proc";

    public QueueProcessor(final CompletionHandler handler, final CompletionQueue... completionQueues) {
        super(THREAD_NAME);
        this.completionQueues = completionQueues.clone();
        this.completionChannel = null;
        this.handler = handler;
    }

    public QueueProcessor(final CompletionHandler handler, final CompletionChannel completionChannel) {
        super(THREAD_NAME);
        this.completionQueues = null;
        this.completionChannel = completionChannel;
        this.handler = handler;
    }

    @Override
    public void close() {
        isRunning = false;
    }

    @Override
    public void run() {
        pollQueue();
        pollChannel();
        LOGGER.info("Queue processor finished");
    }

    private void pollQueue() {
        if (completionQueues == null) {
            return;
        }

        while (isRunning) {
            for (CompletionQueue completionQueue : completionQueues) {
                process(completionQueue);
            }
        }
    }

    private void pollChannel() {
        if (completionChannel == null) {
            return;
        }

        while(isRunning) {
            var completionQueue = completionChannel.getCompletionEvent();
            if (completionQueue == null) {
                continue;
            }

            process(completionQueue);
            completionQueue.requestNotification(false);
        }
    }

    private void process(final CompletionQueue queue) {
        queue.poll(completionArray);
        for(int i = 0; i < completionArray.getLength(); i++) {
            var workCompletion = completionArray.get(i);
            if (workCompletion.getStatus() == WorkCompletion.Status.SUCCESS) {
                notifyComplete(workCompletion.getId());
            } else {
                notifyError(workCompletion.getId(), workCompletion.getStatus());
            }
        }
    }

    private void notifyComplete(final long id) {
        Schedulers.computation(() -> handler.onComplete(id));
    }

    private void notifyError(final long id, final WorkCompletion.Status status) {
        Schedulers.computation(() -> handler.onError(id, status));
    }
}
