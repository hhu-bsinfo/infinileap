package de.hhu.bsinfo.infinileap;

import de.hhu.bsinfo.infinileap.util.EnumConverter;
import org.linux.rdma.ibverbs_h;

public enum TransportType {
    UNKNOWN(ibverbs_h.IBV_TRANSPORT_UNKNOWN()), IB(ibverbs_h.IBV_TRANSPORT_IB()),
    IWARP(ibverbs_h.IBV_TRANSPORT_IWARP()), USNIC(ibverbs_h.IBV_TRANSPORT_USNIC()),
    USNIC_UDP(ibverbs_h.IBV_TRANSPORT_USNIC_UDP()), UNSPECIFIED(ibverbs_h.IBV_TRANSPORT_UNSPECIFIED());

    private final int value;

    TransportType(int value) {
        this.value = value;
    }

    public static final EnumConverter<TransportType> CONVERTER = new EnumConverter<>() {

        @Override
        public int toInt(TransportType enumeration) {
            return enumeration.value;
        }

        @Override
        public TransportType toEnum(int integer) {
            for (var nodeType : values()) {
                if (nodeType.value == integer) {
                    return nodeType;
                }
            }

            throw new IllegalArgumentException(String.format("Unknown transport type provided: %d", integer));
        }
    };

}
