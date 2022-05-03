package de.hhu.bsinfo.infinileap.common.multiplex;

import de.hhu.bsinfo.infinileap.common.io.FileDescriptor;

public interface Watchable {

    /**
     * Returns this instance's file descriptor for registration
     * with I/O multiplexing facilities like epoll.
     */
    FileDescriptor descriptor();
}
