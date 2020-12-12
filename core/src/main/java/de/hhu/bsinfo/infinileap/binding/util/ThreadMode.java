package de.hhu.bsinfo.infinileap.binding.util;

import org.openucx.ucx_h;

public enum ThreadMode {
    SINGLE(ucx_h.UCS_THREAD_MODE_SINGLE()),
    MULTI(ucx_h.UCS_THREAD_MODE_MULTI()),
    SERIALIZED(ucx_h.UCS_THREAD_MODE_SERIALIZED()),
    LAST(ucx_h.UCS_THREAD_MODE_LAST());

    private final int value;

    ThreadMode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
