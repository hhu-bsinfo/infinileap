package de.hhu.bsinfo.infinileap.nio;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.FileDescriptor;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.Status;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import jdk.incubator.foreign.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.linux.rdma.infinileap_h.*;

public class EpollSelector<T> {

    /**
     * The {@link Epoll} instance used by this {@link EpollSelector}.
     */
    private final Epoll epoll;

    /**
     * The mapping between file descriptors and SelectionKeys.
     */
    private final ConcurrentHashMap<Integer, SelectionKey<T>> keyMap = new ConcurrentHashMap<>();

    private EpollSelector(Epoll epoll) {
        this.epoll = epoll;
    }

    public SelectionKey<T> register(FileDescriptor descriptor, EventType... eventTypes) throws IOException {
        return register(descriptor, null, eventTypes);
    }

    public SelectionKey<T> register(FileDescriptor descriptor, T attachment, EventType... eventTypes) throws IOException {

        // Create selection key and add it to the key map
        var selectionKey = new SelectionKey<>(attachment, this);
        selectionKey.interestOps(eventTypes);
        if (keyMap.putIfAbsent(descriptor.intValue(), selectionKey) != null) {
            throw new IllegalArgumentException("FileDescriptor is already registered");
        }

        // Add the file descriptor to the epoll interest list.
        // This must happen after the mapping to ensure the SelectionKey
        // is present when epoll fires.
        epoll.add(descriptor, eventTypes);

        // Return the selection key
        return selectionKey;
    }

    public void select(Consumer<SelectionKey<T>> action, int timeout) throws IOException {
        var count = epoll.wait(timeout);
        for (int i = 0; i < count; i++) {

            // Retrieve event data from epoll instance
            var events = epoll.getEvents(i);
            var data = (int) epoll.getData(i);

            // Get associated selection key and update ready ops
            var selectionKey = keyMap.get(data);
            selectionKey.readyOps(events);

            // Perform user action on selection key
            action.accept(selectionKey);
        }
    }

    public static <T> EpollSelector<T> create() throws IOException {
        return new EpollSelector<>(Epoll.create());
    }
}
