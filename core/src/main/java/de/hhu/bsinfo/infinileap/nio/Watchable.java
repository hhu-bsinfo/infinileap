package de.hhu.bsinfo.infinileap.nio;

import de.hhu.bsinfo.infinileap.util.FileDescriptor;

public interface Watchable {

    /**
     * Returns this instance's file descriptor for registration
     * with I/O multiplexing facilities like epoll.
     */
    FileDescriptor descriptor();
}
