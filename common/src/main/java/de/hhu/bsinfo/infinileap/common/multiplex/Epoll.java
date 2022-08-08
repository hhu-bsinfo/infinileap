package de.hhu.bsinfo.infinileap.common.multiplex;

import de.hhu.bsinfo.infinileap.common.io.FileDescriptor;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.NativeError;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import org.unix.epoll_event;

import java.io.IOException;
import java.time.Duration;

import static org.unix.Linux.*;

public final class Epoll {

    /**
     * The watchlist's default size.
     */
    private static final int DEFAULT_SIZE = 1024;

    /**
     * The default maximum number of events polled in one invocation.
     */
    private static final int POLL_SIZE = 1024;

    /**
     * The epoll add operation flag.
     */
    private static final int OPERATION_ADD = EPOLL_CTL_ADD();

    /**
     * The epoll modify operation flag.
     */
    private static final int OPERATION_MODIFY = EPOLL_CTL_MOD();

    /**
     * The epoll delete operation flag.
     */
    private static final int OPERATION_DELETE = EPOLL_CTL_DEL();

    /**
     * The offset at which the event's data is located.
     */
    private static final long DATA_OFFSET = epoll_event.$LAYOUT()
            .byteOffset(MemoryLayout.PathElement.groupElement("data"));

    /**
     * The epoll event structure's size in bytes.
     */
    private static final long LAYOUT_SIZE = epoll_event.sizeof();

    private final MemorySession session = MemorySession.openImplicit();

    /**
     * The epoll file descriptor used by this epoll instance.
     */
    private final FileDescriptor epfd;

    /**
     * The segment used for polling events.
     */
    private final MemorySegment pollSegment;

    /**
     * The segment used for add and modify operations.
     */
    private final MemorySegment eventSegment = MemorySegment.allocateNative(epoll_event.$LAYOUT(), session);

    private Epoll(FileDescriptor epfd, int pollSize) {
        this.epfd = epfd;
        this.pollSegment = MemorySegment.allocateNative(pollSize * epoll_event.$LAYOUT().byteSize(), session);
    }

    public void add(FileDescriptor fileDescriptor, EventType... eventTypes) throws IOException {
        control(fileDescriptor, OPERATION_ADD, eventTypes);
    }

    public void modify(FileDescriptor fileDescriptor, EventType... eventTypes) throws IOException {
        control(fileDescriptor, OPERATION_MODIFY, eventTypes);
    }

    public void delete(FileDescriptor fileDescriptor) throws IOException {
        control(fileDescriptor, OPERATION_DELETE);
    }

    public int wait(Duration duration) throws IOException {
        var count = epoll_wait(epfd.intValue(), pollSegment, POLL_SIZE, (int) duration.toMillis());
        if (count == NativeError.ERROR) {
            throw new IOException(NativeError.getMessage());
        }

        return count;
    }

    public int getEvents(long index) {
        return epoll_event.events$get(pollSegment, index);
    }

    public long getData(long index) {
        return pollSegment.get(ValueLayout.JAVA_LONG, index * LAYOUT_SIZE + DATA_OFFSET);
    }

    private void control(FileDescriptor fileDescriptor, int operation, EventType... eventTypes) throws IOException {
        control(fileDescriptor, operation, fileDescriptor.intValue(), eventTypes);
    }

    private void control(FileDescriptor fileDescriptor, int operation, long data, EventType... eventTypes) throws IOException {
        // Initialize event
        setEvents(eventSegment, eventTypes);
        setData(eventSegment, data);

        // Execute operation
        if (epoll_ctl(epfd.intValue(), operation, fileDescriptor.intValue(), eventSegment) != NativeError.OK) {
            throw new IOException(NativeError.getMessage());
        }
    }

    public static Epoll create() throws IOException {
        return create(DEFAULT_SIZE);
    }

    public static Epoll create(int size) throws IOException {
        var epfd = epoll_create(size);
        if (epfd == NativeError.ERROR) {
            throw new IOException(NativeError.getMessage());
        }

        var fd = FileDescriptor.of(epfd);
        return new Epoll(fd, POLL_SIZE);
    }

    private static void setEvents(MemorySegment segment, EventType... eventTypes) {
        epoll_event.events$set(segment, BitMask.intOf(eventTypes));
    }

    private static void setData(MemorySegment segment, long data) {
        segment.set(ValueLayout.JAVA_LONG, DATA_OFFSET, data);
    }
}
