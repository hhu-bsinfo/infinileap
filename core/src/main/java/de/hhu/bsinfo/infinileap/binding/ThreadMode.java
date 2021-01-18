package de.hhu.bsinfo.infinileap.binding;

import static org.openucx.ucx_h.*;

public enum ThreadMode {
    SINGLE(UCS_THREAD_MODE_SINGLE()),
    MULTI(UCS_THREAD_MODE_MULTI()),
    SERIALIZED(UCS_THREAD_MODE_SERIALIZED()),
    LAST(UCS_THREAD_MODE_LAST());

    private final int value;

    ThreadMode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
