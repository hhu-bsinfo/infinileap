package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.util.flag.LongFlag;

import java.io.Closeable;
import java.io.IOException;

public class FileDescriptor implements Closeable {

    private final int handle;

    FileDescriptor(int handle) {
        this.handle = handle;
    }

    public final int getHandle() {
        return handle;
    }

    public final void setMode(OpenMode mode) {
        setMode0(handle, mode.value);
    }

    @Override
    public void close() throws IOException {
        if (close0(handle) != 0) {
            throw new IOException("closing file descriptor failed");
        }
    }

    public static FileDescriptor create(int fd) {
        return new FileDescriptor(fd);
    }

    protected static native int close0(int fd);

    protected static native int setMode0(int fd, int mode);

    public enum OpenMode implements LongFlag {
        NONBLOCK(0x0004), APPEND(0x0008), SHLOCK(0x0010), EXLOCK(0x0020), ASYNC(0x0040), FSYNC(0x0080);

        private final int value;

        OpenMode(int value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }
}
