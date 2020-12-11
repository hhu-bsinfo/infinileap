package de.hhu.bsinfo.infinileap.nio;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.FileDescriptor;
import de.hhu.bsinfo.infinileap.util.Status;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;

import java.io.IOException;
import java.time.Duration;

import static org.openucx.ucx_h.*;

public class Epoll {

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
    private final MemorySegment eventSegment = MemorySegment.allocateNative(epoll_event.$LAYOUT());

    private Epoll(FileDescriptor epfd, int pollSize) {
        this.epfd = epfd;
        this.pollSegment = MemorySegment.allocateNative(pollSize * epoll_event.$LAYOUT().byteSize());
    }

    void add(FileDescriptor fileDescriptor, EventType... eventTypes) throws IOException {
        control(fileDescriptor, OPERATION_ADD, eventTypes);
    }

    void modify(FileDescriptor fileDescriptor, EventType... eventTypes) throws IOException {
        control(fileDescriptor, OPERATION_MODIFY, eventTypes);
    }

    void delete(FileDescriptor fileDescriptor) throws IOException {
        control(fileDescriptor, OPERATION_DELETE);
    }

    int wait(Duration duration) throws IOException {
        var count = epoll_wait(epfd.intValue(), pollSegment, POLL_SIZE, (int) duration.toMillis());
        if (count == Status.ERROR) {
            throw new IOException(Status.getErrorMessage());
        }

        return count;
    }

    int getEvents(long index) {
        return epoll_event.events$get(pollSegment, index);
    }

    long getData(long index) {
        return MemoryAccess.getLongAtOffset(pollSegment, index * LAYOUT_SIZE + DATA_OFFSET);
    }

    private void control(FileDescriptor fileDescriptor, int operation, EventType... eventTypes) throws IOException {
        try (var event = MemorySegment.allocateNative(epoll_event.$LAYOUT())) {

            // Initialize event
            setEvents(event, eventTypes);
            setData(event, fileDescriptor.intValue());

            // Execute operation
            if (epoll_ctl(epfd.intValue(), operation, fileDescriptor.intValue(), event) != Status.OK) {
                throw new IOException(Status.getErrorMessage());
            }
        }
    }

    static Epoll create() throws IOException {
        var epfd = epoll_create(DEFAULT_SIZE);
        if (epfd == Status.ERROR) {
            throw new IOException(Status.getErrorMessage());
        }

        var fd = FileDescriptor.from(epfd);
        return new Epoll(fd, POLL_SIZE);
    }

    private static void setEvents(MemorySegment segment, EventType... eventTypes) {
        epoll_event.events$set(segment, BitMask.intOf(eventTypes));
    }

    private static void setData(MemorySegment segment, long data) {
        MemoryAccess.setLongAtOffset(segment, DATA_OFFSET, data);
    }
}
