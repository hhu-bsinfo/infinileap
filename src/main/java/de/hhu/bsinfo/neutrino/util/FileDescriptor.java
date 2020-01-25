package de.hhu.bsinfo.neutrino.util;

public class FileDescriptor {

    private final int handle;

    public FileDescriptor(int handle) {
        this.handle = handle;
    }

    public int get() {
        return handle;
    }
}
