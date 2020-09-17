package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import jdk.incubator.foreign.CSupport;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryLayouts;
import jdk.incubator.foreign.MemorySegment;
import org.linux.rdma.infinileap_h;

import java.io.IOException;

import static org.linux.rdma.infinileap_h.*;

public class EventFileDescriptor extends FileDescriptor {


    private EventFileDescriptor(int fd) {
        super(fd);
    }

    public void reset() throws IOException {
        read();
    }

    public long read() throws IOException {
        try (var counter = MemorySegment.allocateNative(MemoryLayouts.JAVA_LONG)) {
            var bytesRead = eventfd_read(intValue(), counter);
            if (bytesRead == Status.ERROR) {
                throw new IOException(Status.getErrorMessage());
            }

            return MemoryAccess.getLong(counter);
        }
    }

    public void increment(long addend) throws IOException {
        if (eventfd_write(intValue(), addend) == Status.ERROR) {
            throw new IOException(Status.getErrorMessage());
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
        if (descriptor == Status.ERROR) {
            throw new IOException(Status.getErrorMessage());
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
