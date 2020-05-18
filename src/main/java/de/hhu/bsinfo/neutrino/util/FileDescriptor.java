package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import de.hhu.bsinfo.neutrino.util.flag.LongFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

public class FileDescriptor implements Closeable {

    private final int handle;

    FileDescriptor(int handle) {
        this.handle = handle;
    }

    public final int getHandle() {
        return handle;
    }

    public final void setFlags(OpenMode... modes) {
        setFlags0(handle, BitMask.intOf(modes));
    }

    public final OpenMode[] getFlags() {
        var flags = getFlags0(handle);
        return Arrays.stream(OpenMode.values())
                .filter(mode -> BitMask.isSet(flags, mode))
                .toArray(OpenMode[]::new);
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

    protected static native int setFlags0(int fd, int mode);

    protected static native int getFlags0(int fd);

    public enum OpenMode implements IntegerFlag {
        READ_ONLY   (0x000),
        WRITE_ONLY  (0x001),
        READ_WRITE  (0x002),
        CREATE      (0x040),
        EXCLUSIVE   (0x080),
        NOCTTY      (0x100),
        TRUNCATE    (0x200),
        APPEND      (0x400),
        NONBLOCK    (0x800);

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
