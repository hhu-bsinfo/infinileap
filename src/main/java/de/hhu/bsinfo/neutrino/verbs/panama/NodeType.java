package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.struct.field.EnumConverter;
import org.linux.rdma.ibverbs_h;

public enum NodeType {
    UNKNOWN(ibverbs_h.IBV_NODE_UNKNOWN()), CA(ibverbs_h.IBV_NODE_CA()), SWITCH(ibverbs_h.IBV_NODE_SWITCH()),
    ROUTER(ibverbs_h.IBV_NODE_ROUTER()), RNIC(ibverbs_h.IBV_NODE_RNIC()), USNIC(ibverbs_h.IBV_NODE_USNIC()),
    USNIC_UDP(ibverbs_h.IBV_NODE_USNIC_UDP()), UNSPECIFIED(ibverbs_h.IBV_NODE_UNSPECIFIED());

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

            throw new IllegalArgumentException(String.format("Unknown node type provided: %d", integer));
        }
    };
}
