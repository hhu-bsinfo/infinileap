package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeArray;
import de.hhu.bsinfo.neutrino.data.NativeIntegerBitMask;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;

public final class Epoll {

    private static final int DEFAULT_SIZE = 1024;

    private final FileDescriptor epfd;

    private Epoll(FileDescriptor epfd) {
        this.epfd = epfd;
    }

    public static Epoll create() {
        return create(DEFAULT_SIZE);
    }

    public static Epoll create(int size) {
        // TODO(krakowski):
        //  Check errors
        var fd = FileDescriptor.create(create0(size));
        return new Epoll(fd);
    }

    public void add(FileDescriptor fd, long data, EventType... eventTypes) {
        var event = new Event(data, eventTypes);
        control0(epfd.getHandle(), Operation.ADD.value, fd.getHandle(), event.getHandle());
    }

    public void modify(FileDescriptor fd, long data, EventType... eventTypes) {
        var event = new Event(data, eventTypes);
        control0(epfd.getHandle(), Operation.MODIFY.value, fd.getHandle(), event.getHandle());
    }

    public void delete(FileDescriptor fd) {
        control0(epfd.getHandle(), Operation.DELETE.value, fd.getHandle(), 1);
    }

    public void wait(EventArray events, int timeout) {
        // TODO(krakowski):
        //  Check errors
        var length = wait0(epfd.getHandle(), events.getHandle(), events.getCapacity(), timeout);
        events.setLength(length);
    }

    private static native int create0(int size);

    private static native int control0(int epfd, int op, int fd, long event);

    private static native int wait0(int epfd, long events, int maxEvents, int timeout);

    @LinkNative("epoll_event")
    public static class Event extends Struct {
        private final NativeIntegerBitMask<EventType> eventTypes = integerBitField("events");
        private final NativeLong data = longField("data");

        public Event(long data, EventType... eventTypes) {
            this.eventTypes.set(eventTypes);
            this.data.set(data);
        }

        public Event(long handle) {
            super(handle);
        }

        public long getData() {
            return data.get();
        }
    }

    public enum EventType implements Flag {
        EPOLLIN(0x001), EPOLLPRI(0x002), EPOLLOUT(0x004), EPOLLRDNORM(0x040), EPOLLRDBAND(0x080), EPOLLWRNORM(0x100),
        EPOLLWRBAND(0x200), EPOLLMSG(0x400), EPOLLERR(0x008), EPOLLHUP(0x010), EPOLLRDHUP(0x2000), EPOLLONESHOT(1 << 30),
        EPOLLET(1 << 31);

        private final int value;

        EventType(int value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    public enum Operation {
        ADD(1), MODIFY(2), DELETE(3);

        private final int value;

        Operation(int value) {
            this.value = value;
        }
    }

    public static class EventArray extends NativeArray<Event> {

        private int length;

        public EventArray(long handle, int capacity) {
            super(Event::new, Event.class, handle, capacity);
        }

        public EventArray(int capacity) {
            super(Event::new, Event.class, capacity);
        }

        @Override
        public Event get(int index) {
            if (index >= length) {
                throw new IndexOutOfBoundsException(String.format("Index %d is outside array content with length %d", index, length));
            }

            return super.get(index);
        }

        public int getLength() {
            return length;
        }

        void setLength(int length) {
            this.length = length;
        }
    }
}
