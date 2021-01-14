package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import org.openucx.ucx_h;

public enum AtomicOperation implements IntegerFlag {
    ADD(ucx_h.UCP_ATOMIC_OP_ADD()),
    SWAP(ucx_h.UCP_ATOMIC_OP_SWAP()),
    COMPARE_AND_SWAP(ucx_h.UCP_ATOMIC_OP_CSWAP()),
    AND(ucx_h.UCP_ATOMIC_OP_AND()),
    OR(ucx_h.UCP_ATOMIC_OP_OR()),
    XOR(ucx_h.UCP_ATOMIC_OP_XOR());

    private final int value;

    AtomicOperation(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
