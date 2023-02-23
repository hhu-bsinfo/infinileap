package de.hhu.bsinfo.infinileap.common.multiplex;

import de.hhu.bsinfo.infinileap.common.util.ThrowingConsumer;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EpollSelector<T> {

    /**
     * The {@link Duration} instance used for blocking epoll wait invocations.
     */
    private static final Duration DURATION_INDEFINITE = Duration.ofMillis(-1);

    /**
     * An {@link EventFileDescriptor} used for waking up the epoll instance manually.
     */
    private final EventFileDescriptor notifier;

    /**
     * The event notifiers selection key.
     */
    private final SelectionKey<T> notifierKey;

    /**
     * The {@link Epoll} instance used by this {@link EpollSelector}.
     */
    private final Epoll epoll;

    /**
     * The mapping between file descriptors and SelectionKeys.
     */
    private final Map<Integer, SelectionKey<T>> keyMap = new ConcurrentHashMap<>();

    private EpollSelector(Epoll epoll, EventFileDescriptor notifier) {
        this.epoll = epoll;
        this.notifier = notifier;

        try {
            this.notifierKey = register(notifier, EventType.EPOLLIN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SelectionKey<T> register(Watchable watchable, EventType... eventTypes) throws IOException {
        return register(watchable, null, eventTypes);
    }

    public SelectionKey<T> register(Watchable watchable, T attachment, EventType... eventTypes) throws IOException {

        // Create selection key and set interest ops
        var selectionKey = new SelectionKey<>(watchable, attachment, this);
        selectionKey.setInterestOps(eventTypes);

        // Extract descriptor and add it to the key map
        var descriptor = watchable.descriptor();
        if (keyMap.putIfAbsent(descriptor.intValue(), selectionKey) != null) {
            throw new IllegalArgumentException("FileDescriptor is already registered");
        }

        // Add the file descriptor to the epoll interest list.
        // This MUST happen after the mapping to ensure the SelectionKey
        // is present when epoll fires.
        epoll.add(descriptor, eventTypes);

        // Return the selection key
        return selectionKey;
    }

    public int select(ThrowingConsumer<SelectionKey<T>, IOException> action) throws IOException {
        return select(action, DURATION_INDEFINITE);
    }

    public int select(ThrowingConsumer<SelectionKey<T>, IOException> action, Duration duration) throws IOException {
        var count = epoll.wait(duration);
        for (int i = 0; i < count; i++) {

            // Retrieve event data from epoll instance
            var events = epoll.getEvents(i);
            var data = (int) epoll.getData(i);

            // Skip manually triggered events
            if (data == notifier.intValue()) {
                notifier.reset();
                continue;
            }

            // Get associated selection key and update ready ops
            var selectionKey = keyMap.get(data);
            selectionKey.readyOps(events);

            // Perform user action on selection key
            action.accept(selectionKey);
        }

        return count;
    }

    public void wake() throws IOException {
        notifier.fire();
    }

    public static <T> EpollSelector<T> create() throws IOException {
        return new EpollSelector<>(Epoll.create(), EventFileDescriptor.create(EventFileDescriptor.OpenMode.NONBLOCK));
    }
}
