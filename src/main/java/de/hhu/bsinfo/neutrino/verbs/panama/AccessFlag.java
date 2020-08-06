package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import org.linux.rdma.ibverbs_h;

public enum AccessFlag implements IntegerFlag {
    LOCAL_WRITE(ibverbs_h.IBV_ACCESS_LOCAL_WRITE()), REMOTE_WRITE(ibverbs_h.IBV_ACCESS_REMOTE_WRITE()),
    REMOTE_READ(ibverbs_h.IBV_ACCESS_REMOTE_READ()), REMOTE_ATOMIC(ibverbs_h.IBV_ACCESS_REMOTE_ATOMIC()),
    MW_BIND(ibverbs_h.IBV_ACCESS_MW_BIND()), ZERO_BASED(ibverbs_h.IBV_ACCESS_ZERO_BASED()),
    ON_DEMAND(ibverbs_h.IBV_ACCESS_ON_DEMAND());

    private final int value;

    AccessFlag(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}