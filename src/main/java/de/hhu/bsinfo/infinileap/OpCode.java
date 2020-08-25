package de.hhu.bsinfo.infinileap;

import de.hhu.bsinfo.infinileap.util.EnumConverter;
import org.linux.rdma.ibverbs_h;

public enum OpCode {
    RDMA_WRITE(ibverbs_h.IBV_WR_RDMA_WRITE()), RDMA_WRITE_WITH_IMM(ibverbs_h.IBV_WR_RDMA_WRITE_WITH_IMM()),
    SEND(ibverbs_h.IBV_WR_SEND()), SEND_WITH_IMM(ibverbs_h.IBV_WR_SEND_WITH_IMM()),
    RDMA_READ(ibverbs_h.IBV_WR_RDMA_READ()), ATOMIC_CMP_AND_SWP(ibverbs_h.IBV_WR_ATOMIC_CMP_AND_SWP()),
    ATOMIC_FETCH_AND_ADD(ibverbs_h.IBV_WR_ATOMIC_FETCH_AND_ADD()), LOCAL_INV(ibverbs_h.IBV_WR_LOCAL_INV()),
    BIND_MW(ibverbs_h.IBV_WR_BIND_MW()), SEND_WITH_INV(ibverbs_h.IBV_WR_SEND_WITH_INV()), TSO(ibverbs_h.IBV_WR_TSO());

    private final int value;

    OpCode(int value) {
        this.value = value;
    }

    public static final EnumConverter<OpCode> CONVERTER = new EnumConverter<>() {

        @Override
        public int toInt(OpCode enumeration) {
            return enumeration.value;
        }

        @Override
        public OpCode toEnum(int integer) {
            for (var nodeType : values()) {
                if (nodeType.value == integer) {
                    return nodeType;
                }
            }

            throw new IllegalArgumentException(String.format("Unknown op code provided: %d", integer));
        }
    };
}
