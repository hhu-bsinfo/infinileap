package de.hhu.bsinfo.infinileap.verbs;

import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import org.linux.rdma.infinileap_h;

public enum AccessFlag implements IntegerFlag {
    LOCAL_WRITE(infinileap_h.IBV_ACCESS_LOCAL_WRITE()), REMOTE_WRITE(infinileap_h.IBV_ACCESS_REMOTE_WRITE()),
    REMOTE_READ(infinileap_h.IBV_ACCESS_REMOTE_READ()), REMOTE_ATOMIC(infinileap_h.IBV_ACCESS_REMOTE_ATOMIC()),
    MW_BIND(infinileap_h.IBV_ACCESS_MW_BIND()), ZERO_BASED(infinileap_h.IBV_ACCESS_ZERO_BASED()),
    ON_DEMAND(infinileap_h.IBV_ACCESS_ON_DEMAND());

    private final int value;

    AccessFlag(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}