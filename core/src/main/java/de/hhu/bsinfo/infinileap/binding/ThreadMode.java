package de.hhu.bsinfo.infinileap.binding;

import java.util.Arrays;

import static org.openucx.OpenUcx.*;

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

    static ThreadMode from(int value) {
        return Arrays.stream(values())
                .filter(it -> it.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
