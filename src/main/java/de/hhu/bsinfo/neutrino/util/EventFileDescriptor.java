package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import de.hhu.bsinfo.neutrino.util.flag.LongFlag;
import java.io.IOException;

public final class EventFileDescriptor extends FileDescriptor {

    private static final int STATUS_OK = 0;
    private static final int STATUS_ERROR = -1;

    private EventFileDescriptor(int handle) {
        super(handle);
    }

    /**
     * Creates a new file descriptor for event notification.
     */
    public static EventFileDescriptor create(OpenMode... modes) {
        return create(0, modes);
    }

    /**
     * Creates a new file descriptor for event notification using the specified initial counter value.
     */
    public static EventFileDescriptor create(int counter, OpenMode... modes) {
        var flags = BitMask.intOf(modes);
        var handle = create0(counter, flags);
        if (handle == STATUS_ERROR) {
            throw new IllegalArgumentException("Creating file descriptor failed", SystemUtil.lastError());
        }

        return new EventFileDescriptor(handle);
    }

    /**
     * Resets the file descriptor's internal counter.
     */
    public void reset() {
        read();
    }

    /**
     * Reads the file descriptor's internal counter.
     */
    public long read() {
        var value = read0(getHandle());
        if (value == STATUS_ERROR) {
            throw new IllegalArgumentException("Reading counter failed", SystemUtil.lastError());
        }

        return value;
    }


    public void fire() {
        increment();
    }

    /**
     * Increments the file descriptor's internal counter
     */
    public void increment() {
        increment(1);
    }

    /**
     * Increments the file descriptor's internal counter by the specified value.
     */
    public void increment(long value) {
        var handle = getHandle();
        if (increment0(handle, value) != STATUS_OK) {
            throw new IllegalArgumentException("Incrementing counter failed", SystemUtil.lastError());
        }
    }

    private static native int create0(int count, int flags);

    private static native long read0(int fd);

    private static native int increment0(int fd, long value);

    @Override
    public void close() throws IOException {
        var handle = getHandle();
        if (close0(handle) != 0) {
            throw new IOException("closing file descriptor failed");
        }
    }

    public enum OpenMode implements IntegerFlag {
        NONBLOCK(0x800), CLOEXEC(0x80000), SEMAPHORE(0x1);

        private final int value;

        OpenMode(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
