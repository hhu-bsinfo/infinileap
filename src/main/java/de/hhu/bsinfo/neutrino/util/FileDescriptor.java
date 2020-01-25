package de.hhu.bsinfo.neutrino.util;

import java.io.Closeable;
import java.io.IOException;

public class FileDescriptor implements Closeable {

    private final int handle;

    public FileDescriptor(int handle) {
        this.handle = handle;
    }

    public int get() {
        return handle;
    }

    private static native int close0(int fd);

    @Override
    public void close() throws IOException {
        if (close0(handle) != 0) {
            throw new IOException("closing file descriptor failed");
        }
    }
}
