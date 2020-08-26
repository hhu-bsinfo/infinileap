package de.hhu.bsinfo.infinileap.communication;

import de.hhu.bsinfo.infinileap.util.EnumConverter;
import org.linux.rdma.infinileap_h;

import java.util.Arrays;

public enum PortSpace {
    IPOIB(infinileap_h.RDMA_PS_IPOIB()), TCP(infinileap_h.RDMA_PS_TCP()),
    UDP(infinileap_h.RDMA_PS_UDP()), IB(infinileap_h.RDMA_PS_IB());

    private static final PortSpace[] VALUES;

    static {
        int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

        VALUES = new PortSpace[arrayLength];

        for (PortSpace element : PortSpace.values()) {
            VALUES[element.value] = element;
        }
    }

    private final int value;

    PortSpace(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static final EnumConverter<PortSpace> CONVERTER = new EnumConverter<>() {

        @Override
        public int toInt(PortSpace enumeration) {
            return enumeration.value;
        }

        @Override
        public PortSpace toEnum(int integer) {
            if (integer != IPOIB.value && integer != TCP.value && integer != UDP.value && integer != IB.value) {
                throw new IllegalArgumentException(String.format("Unknown port space code %d", integer));
            }

            return VALUES[integer];
        }
    };
}
