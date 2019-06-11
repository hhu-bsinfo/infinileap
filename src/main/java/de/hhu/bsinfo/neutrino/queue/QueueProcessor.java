package de.hhu.bsinfo.neutrino.queue;

import de.hhu.bsinfo.neutrino.scheduler.Schedulers;
import de.hhu.bsinfo.neutrino.verbs.CompletionChannel;
import de.hhu.bsinfo.neutrino.verbs.CompletionQueue;
import de.hhu.bsinfo.neutrino.verbs.CompletionQueue.WorkCompletionArray;
import de.hhu.bsinfo.neutrino.verbs.WorkCompletion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class QueueProcessor extends Thread implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueProcessor.class);

    public interface Listener {
        void onComplete(long id);
        void onError(long id, WorkCompletion.Status status);
    }

    private final CompletionQueue[] completionQueues;
    private final CompletionChannel completionChannel;

    private final WorkCompletionArray completionArray = new WorkCompletionArray(100);
    private final Listener listener;

    private transient boolean isRunning = true;

    private static final String THREAD_NAME = "proc";

    public QueueProcessor(final Listener listener, final CompletionQueue... completionQueues) {
        super(THREAD_NAME);
        this.completionQueues = completionQueues;
        this.completionChannel = null;
        this.listener = listener;
    }

    public QueueProcessor(final Listener listener, final CompletionChannel completionChannel) {
        super(THREAD_NAME);
        this.completionQueues = null;
        this.completionChannel = completionChannel;
        this.listener = listener;
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
        WorkCompletion workCompletion;
        for(int i = 0; i < completionArray.getLength(); i++) {
            workCompletion = completionArray.get(i);
            if (workCompletion.getStatus() == WorkCompletion.Status.SUCCESS) {
                notifyComplete(workCompletion.getId());
            } else {
                notifyError(workCompletion.getId(), workCompletion.getStatus());
            }
        }
    }

    private void notifyComplete(final long id) {
        Schedulers.computation(() -> listener.onComplete(id));
    }

    private void notifyError(final long id, final WorkCompletion.Status status) {
        Schedulers.computation(() -> listener.onError(id, status));
    }
}
