package de.hhu.bsinfo.infinileap.common.multiplex;

import de.hhu.bsinfo.infinileap.common.io.FileDescriptor;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.NativeError;
import de.hhu.bsinfo.infinileap.common.util.flag.IntegerFlag;

import javax.swing.text.Segment;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

import java.io.IOException;

import static org.unix.Linux.*;

public final class EventFileDescriptor extends FileDescriptor {

    private final SegmentScope session = SegmentScope.auto();

    private final MemorySegment counter = MemorySegment.allocateNative(ValueLayout.JAVA_LONG, session);

    private EventFileDescriptor(int fd) {
        super(fd);
    }

    public void reset() throws IOException {
        read();
    }

    public long read() throws IOException {
        var bytesRead = eventfd_read(intValue(), counter);
        if (bytesRead == NativeError.ERROR) {
            throw new IOException(NativeError.getMessage());
        }

        return counter.get(ValueLayout.JAVA_LONG, 0L);
    }

    public void increment(long addend) throws IOException {
        if (eventfd_write(intValue(), addend) == NativeError.ERROR) {
            throw new IOException(NativeError.getMessage());
        }
    }

    public void fire() throws IOException {
        increment(1);
    }

    public static EventFileDescriptor create(OpenMode... modes) throws IOException {
        return create(0, modes);
    }

    public static EventFileDescriptor create(int counter, OpenMode... modes) throws IOException {
        var openMode = BitMask.intOf(modes);
        var descriptor = eventfd(counter, openMode);
        if (descriptor == NativeError.ERROR) {
            throw new IOException(NativeError.getMessage());
        }

        return new EventFileDescriptor(descriptor);
    }

    public enum OpenMode implements IntegerFlag {
        NONBLOCK(EFD_NONBLOCK()),
        CLOEXEC(EFD_CLOEXEC()),
        SEMAPHORE(EFD_SEMAPHORE());

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
