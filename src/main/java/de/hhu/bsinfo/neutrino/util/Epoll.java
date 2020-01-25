package de.hhu.bsinfo.neutrino.util;

public class Epoll {

    private static native int create(int size);

    private static native int control(int epfd, int op, int fd, long event);

    private static native int wait(int epfd, long events, int maxEvents, int timeout);
}
