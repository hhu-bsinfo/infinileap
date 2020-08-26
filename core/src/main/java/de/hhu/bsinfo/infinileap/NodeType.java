package de.hhu.bsinfo.infinileap;

import de.hhu.bsinfo.infinileap.util.EnumConverter;
import org.linux.rdma.infinileap_h;

public enum NodeType {
    UNKNOWN(infinileap_h.IBV_NODE_UNKNOWN()), CA(infinileap_h.IBV_NODE_CA()), SWITCH(infinileap_h.IBV_NODE_SWITCH()),
    ROUTER(infinileap_h.IBV_NODE_ROUTER()), RNIC(infinileap_h.IBV_NODE_RNIC()), USNIC(infinileap_h.IBV_NODE_USNIC()),
    USNIC_UDP(infinileap_h.IBV_NODE_USNIC_UDP()), UNSPECIFIED(infinileap_h.IBV_NODE_UNSPECIFIED());

    private final int value;

    NodeType(int value) {
        this.value = value;
    }

    public static final EnumConverter<NodeType> CONVERTER = new EnumConverter<>() {

        @Override
        public int toInt(NodeType enumeration) {
            return enumeration.value;
        }

        @Override
        public NodeType toEnum(int integer) {
            for (var nodeType : values()) {
                if (nodeType.value == integer) {
                    return nodeType;
                }
            }

            return UNKNOWN;
        }
    };
}
