package de.hhu.bsinfo.infinileap.common.multiplex;

import de.hhu.bsinfo.infinileap.common.io.FileDescriptor;

public interface Watchable {

    String NO_NAME = "";

    /**
     * Returns this instance's file descriptor for registration
     * with I/O multiplexing facilities like epoll.
     */
    FileDescriptor descriptor();

    default String name() {
        return NO_NAME;
    }
}
