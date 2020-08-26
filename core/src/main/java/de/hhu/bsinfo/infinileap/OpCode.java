package de.hhu.bsinfo.infinileap;

import de.hhu.bsinfo.infinileap.util.EnumConverter;
import org.linux.rdma.infinileap_h;

public enum OpCode {
    RDMA_WRITE(infinileap_h.IBV_WR_RDMA_WRITE()), RDMA_WRITE_WITH_IMM(infinileap_h.IBV_WR_RDMA_WRITE_WITH_IMM()),
    SEND(infinileap_h.IBV_WR_SEND()), SEND_WITH_IMM(infinileap_h.IBV_WR_SEND_WITH_IMM()),
    RDMA_READ(infinileap_h.IBV_WR_RDMA_READ()), ATOMIC_CMP_AND_SWP(infinileap_h.IBV_WR_ATOMIC_CMP_AND_SWP()),
    ATOMIC_FETCH_AND_ADD(infinileap_h.IBV_WR_ATOMIC_FETCH_AND_ADD()), LOCAL_INV(infinileap_h.IBV_WR_LOCAL_INV()),
    BIND_MW(infinileap_h.IBV_WR_BIND_MW()), SEND_WITH_INV(infinileap_h.IBV_WR_SEND_WITH_INV()), TSO(infinileap_h.IBV_WR_TSO());

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
