package de.hhu.bsinfo.infinileap.nio;

import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;

import static org.openucx.ucx_h.*;

public enum EventType implements IntegerFlag {
    EPOLLIN(EPOLLIN()),
    EPOLLPRI(EPOLLPRI()),
    EPOLLOUT(EPOLLOUT()),
    EPOLLRDNORM(EPOLLRDNORM()),
    EPOLLRDBAND(EPOLLRDBAND()),
    EPOLLWRNORM(EPOLLWRNORM()),
    EPOLLWRBAND(EPOLLWRBAND()),
    EPOLLMSG(EPOLLMSG()),
    EPOLLERR(EPOLLERR()),
    EPOLLHUP(EPOLLHUP()),
    EPOLLRDHUP(EPOLLRDHUP()),
    EPOLLONESHOT(EPOLLONESHOT()),
    EPOLLET(EPOLLET());

    private final int value;

    EventType(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
