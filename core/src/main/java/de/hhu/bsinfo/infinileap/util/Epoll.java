package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.io.IOException;

import static org.linux.rdma.infinileap_h.*;

public class Epoll {

    /**
     * The watchlist's default size.
     */
    private static final int DEFAULT_SIZE = 1024;

    /**
     * The epoll file descriptor used by this epoll instance.
     */
    private final FileDescriptor epfd;

    private Epoll(FileDescriptor epfd) {
        this.epfd = epfd;
    }

    public static Epoll create() throws IOException {
        return create(DEFAULT_SIZE);
    }

    public static Epoll create(int size) throws IOException {
        var epfd = epoll_create(size);
        if (epfd == Status.ERROR) {
            throw new IOException(Status.getErrorMessage());
        }

        var fd = FileDescriptor.from(epfd);
        return new Epoll(fd);
    }

    public static class Event extends NativeObject {

        protected Event(MemoryAddress address) {
            super(address, epoll_event.$LAYOUT());
        }

        protected Event(MemorySegment segment) {
            super(segment);
        }

        public boolean hasType(EventType type) {
            return BitMask.isSet(epoll_event.events$get(segment()), type);
        }

        public long getData() {
            return MemoryAccess.getLong(epoll_event.data$slice(segment()));
        }
    }

    public enum EventType implements IntegerFlag {
        EPOLLIN(EPOLLIN()),
        EPOLLPRI(EPOLLPRI()),
        EPOLLOUT(EPOLLOUT()),
        EPOLLRDNORM(EPOLLRDNORM()),
        EPOLLRDBAND(EPOLLRDBAND()),
        EPOLLWRNORM(EPOLLWRNORM()),
        EPOLLWRBAND(EPOLLWRBAND()),
        EPOLLMSG(EPOLLMSG()),
        EPOLLERR(EPOLLERR()),
        EPOLLHUP(EPOLLHUP()),
        EPOLLRDHUP(EPOLLRDHUP()),
        EPOLLONESHOT(EPOLLONESHOT()),
        EPOLLET(EPOLLET());

        private final int value;

        EventType(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
