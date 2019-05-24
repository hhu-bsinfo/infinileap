package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.util.Flag;

public enum SendFlag implements Flag {
    FENCE(1), SIGNALED(1 << 1), SOLICITED(1 << 2), INLINE(1 << 3), IP_CSUM(1 << 4);

    private final int value;

    SendFlag(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
