package de.hhu.bsinfo.infinileap.engine.event.loop;

import de.hhu.bsinfo.infinileap.common.multiplex.EpollSelector;
import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.common.multiplex.Watchable;
import de.hhu.bsinfo.infinileap.common.util.ThrowingConsumer;
import de.hhu.bsinfo.infinileap.engine.event.loop.AbstractEventLoop;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.ManyToOneConcurrentArrayQueue;
import org.agrona.concurrent.QueuedPipe;
import org.agrona.hints.ThreadHints;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public abstract class EpollEventLoop<T> extends AbstractEventLoop {

    private static final int MAX_CONNECTIONS = 1024;

    /**
     * Incoming connections which should be watched by this agent.
     */
    private final QueuedPipe<WatchRequest<T>> requestPipe = new ManyToOneConcurrentArrayQueue<>(MAX_CONNECTIONS);

    /**
     * Selector used for processing events.
     */
    private final EpollSelector<T> selector;

    /**
     * Method reference for connection processor function.
     */
    private final ThrowingConsumer<SelectionKey<T>, IOException> consumer = this::process;

    protected EpollEventLoop() {
        try {
            selector = EpollSelector.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onLoop() throws Exception {
        // Add new connections to our watch list
        if (!requestPipe.isEmpty()) {
            requestPipe.drain(this::watch);
        }

        selector.select(consumer);
    }

    private void watch(WatchRequest<T> request) {
        if (request.watchable.name() != Watchable.NO_NAME) {
            log.debug("Registering {} for {}", request.watchable.name(), Arrays.toString(request.getEventTypes()));
        } else {
            log.debug("Registering for {}", Arrays.toString(request.getEventTypes()));
        }

        try {
            selector.register(request.getWatchable(), request.getAttachment(), request.getEventTypes());
        } catch (IOException e) {
            log.error("Registering watchable failed");
        }
    }

    /**
     * Adds the connection to this agent's watch list.
     */
    public final void add(Watchable watchable, T attachment, EventType... eventTypes) throws IOException {
        // Add connection, so it will be picked up and added on the next work cycle
        var request = new WatchRequest<>(watchable, attachment, eventTypes);
        while (!requestPipe.offer(request)) {
            ThreadHints.onSpinWait();
        }

        selector.wake();
    }

    /**
     * Called every time a key becomes ready (readable/writeable).
     */
    protected abstract void process(SelectionKey<T> selectionKey) throws IOException;

    public static final @Data class WatchRequest<T> {
        private final Watchable watchable;
        private final T attachment;
        private final EventType[] eventTypes;
    }
}
