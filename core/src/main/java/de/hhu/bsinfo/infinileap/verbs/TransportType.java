package de.hhu.bsinfo.infinileap.verbs;

import de.hhu.bsinfo.infinileap.util.EnumConverter;
import org.linux.rdma.infinileap_h;

public enum TransportType {
    UNKNOWN(infinileap_h.IBV_TRANSPORT_UNKNOWN()), IB(infinileap_h.IBV_TRANSPORT_IB()),
    IWARP(infinileap_h.IBV_TRANSPORT_IWARP()), USNIC(infinileap_h.IBV_TRANSPORT_USNIC()),
    USNIC_UDP(infinileap_h.IBV_TRANSPORT_USNIC_UDP()), UNSPECIFIED(infinileap_h.IBV_TRANSPORT_UNSPECIFIED());

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
