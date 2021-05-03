package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;

import static org.openucx.OpenUcx.*;

public enum AtomicOperation implements IntegerFlag {
    ADD(UCP_ATOMIC_OP_ADD()),
    SWAP(UCP_ATOMIC_OP_SWAP()),
    COMPARE_AND_SWAP(UCP_ATOMIC_OP_CSWAP()),
    AND(UCP_ATOMIC_OP_AND()),
    OR(UCP_ATOMIC_OP_OR()),
    XOR(UCP_ATOMIC_OP_XOR());

    private final int value;

    AtomicOperation(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
