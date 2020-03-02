package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.util.flag.LongFlag;

public enum AccessFlag implements LongFlag {
    LOCAL_WRITE(1), REMOTE_WRITE(1 << 1), REMOTE_READ(1 << 2),
    REMOTE_ATOMIC(1 << 3), MW_BIND(1 << 4), ZERO_BASED(1 << 5),
    ON_DEMAND(1 << 6);

    private final int value;

    AccessFlag(int value) {
        this.value = value;
    }

    @Override
    public long getValue() {
        return value;
    }
}