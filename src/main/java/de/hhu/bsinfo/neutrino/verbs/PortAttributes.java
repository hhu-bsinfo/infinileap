package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.*;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.flag.LongFlag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import java.util.Arrays;

@LinkNative("ibv_port_attr")
public class PortAttributes extends Struct {

    private final NativeEnum<State> state = enumField("state", State.CONVERTER);
    private final NativeEnum<Mtu> maxMtu = enumField("max_mtu", Mtu.CONVERTER);
    private final NativeEnum<Mtu> activeMtu = enumField("active_mtu", Mtu.CONVERTER);
    private final NativeInteger gidTableLength = integerField("gid_tbl_len");
    private final NativeIntegerBitMask<CapabilityFlag> portCapabilities = integerBitField("port_cap_flags");
    private final NativeInteger maxMessageSize = integerField("max_msg_sz");
    private final NativeInteger badPkeyCounter = integerField("bad_pkey_cntr");
    private final NativeInteger qkeyViolationCounter = integerField("qkey_viol_cntr");
    private final NativeShort pkeyTableLength = shortField("pkey_tbl_len");
    private final NativeShort localId = shortField("lid");
    private final NativeShort subnetManagerLocalId = shortField("sm_lid");
    private final NativeByte localIdMask = byteField("lmc");
    private final NativeByte maxVirtualLaneCount = byteField("max_vl_num");
    private final NativeByte subnetManagerServiceLevel = byteField("sm_sl");
    private final NativeByte subnetTimeout = byteField("subnet_timeout");
    private final NativeByte initTypeReply = byteField("init_type_reply");
    private final NativeByte activeWidth = byteField("active_width");
    private final NativeByte activeSpeed = byteField("active_speed");
    private final NativeByte physicalState = byteField("phys_state");
    private final NativeByte linkLayer = byteField("link_layer");
    private final NativeByte flags = byteField("flags");
    private final NativeShortBitMask<ExtendedCapabilityFlag> portCapabilites2 = shortBitField("port_cap_flags2");

    PortAttributes() {}

    PortAttributes(long handle) {
        super(handle);
    }

    public State getState() {
        return state.get();
    }

    public Mtu getMaxMtu() {
        return maxMtu.get();
    }

    public Mtu getActiveMtu() {
        return activeMtu.get();
    }

    public int getGidTableLength() {
        return gidTableLength.get();
    }

    public int getPortCapabilities() {
        return portCapabilities.get();
    }

    public int getMaxMessageSize() {
        return maxMessageSize.get();
    }

    public int getBadPkeyCounter() {
        return badPkeyCounter.get();
    }

    public int getQkeyViolationCounter() {
        return qkeyViolationCounter.get();
    }

    public short getPkeyTableLength() {
        return pkeyTableLength.get();
    }

    public short getLocalId() {
        return localId.get();
    }

    public short getSubnetManagerLocalId() {
        return subnetManagerLocalId.get();
    }

    public byte getLocalIdMask() {
        return localIdMask.get();
    }

    public byte getMaxVirtualLaneCount() {
        return maxVirtualLaneCount.get();
    }

    public byte getSubnetManagerServiceLevel() {
        return subnetManagerServiceLevel.get();
    }

    public byte getSubnetTimeout() {
        return subnetTimeout.get();
    }

    public byte getInitTypeReply() {
        return initTypeReply.get();
    }

    public byte getActiveWidth() {
        return activeWidth.get();
    }

    public byte getActiveSpeed() {
        return activeSpeed.get();
    }

    public byte getPhysicalState() {
        return physicalState.get();
    }

    public byte getLinkLayer() {
        return linkLayer.get();
    }

    public byte getFlags() {
        return flags.get();
    }

    public short getPortCapabilites2() {
        return portCapabilites2.get();
    }

    @Override
    public String toString() {
        return "PortAttributes {" +
            "\n\tstate=" + state +
            ",\n\tmaxMtu=" + maxMtu +
            ",\n\tactiveMtu=" + activeMtu +
            ",\n\tgidTableLength=" + gidTableLength +
            ",\n\tportCapabilities=" + portCapabilities +
            ",\n\tmaxMessageSize=" + maxMessageSize +
            ",\n\tbadPkeyCounter=" + badPkeyCounter +
            ",\n\tqkeyViolationCounter=" + qkeyViolationCounter +
            ",\n\tpkeyTableLength=" + pkeyTableLength +
            ",\n\tlocalId=" + localId +
            ",\n\tsubnetManagerLocalId=" + subnetManagerLocalId +
            ",\n\tlocalIdMask=" + localIdMask +
            ",\n\tmaxVirtualLaneCount=" + maxVirtualLaneCount +
            ",\n\tsubnetManagerServiceLevel=" + subnetManagerServiceLevel +
            ",\n\tsubnetTimeout=" + subnetTimeout +
            ",\n\tinitTypeReply=" + initTypeReply +
            ",\n\tactiveWidth=" + activeWidth +
            ",\n\tactiveSpeed=" + activeSpeed +
            ",\n\tphysicalState=" + physicalState +
            ",\n\tlinkLayer=" + linkLayer +
            ",\n\tflags=" + flags +
            ",\n\tportCapabilites2=" + portCapabilites2 +
            "\n}";
    }

    public enum State {
        NOP(0), DOWN(1), INIT(2), ARMED(3), ACTIVE(4), DEFER(5);

        private static final State[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new State[arrayLength];

            for (State element : State.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<State> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(State enumeration) {
                return enumeration.value;
            }

            @Override
            public State toEnum(int integer) {
                if (integer < NOP.value || integer > DEFER.value) {
                    throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum CapabilityFlag implements LongFlag {
        SM(1 << 1), NOTICE_SUP(1 << 2), TRAP_SUP(1 << 3), OPT_IPD_SUP(1 << 4),
        AUTO_MIGR_SUP(1 << 5), SL_MAP_SUP(1 << 6), MKEY_NVRAM(1 << 7), PKEY_NVRAM(1 << 8),
        LED_INFO_SUP(1 << 9), SYS_IMAGE_GUID_SUP(1 << 11), PKEY_SW_EXT_PORT_TRAP_SUP(1 << 12), EXTENDED_SPEEDS_SUP(1 << 14),
        CAP_MASK2_SUP(1 << 15), CM_SUP(1 << 16), SNMP_TUNNEL_SUP(1 << 17), REINIT_SUP(1 << 18),
        DEVICE_MGMT_SUP(1 << 19), VENDOR_CLASS_SUP(1 << 20), DR_NOTICE_SUP(1 << 21), CAP_MASK_NOTICE_SUP(1 << 22),
        BOOT_MGMT_SUP(1 << 23), LINK_LATENCY_SUP(1 << 24), CLIENT_REG_SUP(1 << 25), IP_BASED_GIDS(1 << 26);

        private final int value;

        CapabilityFlag(int value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    public enum ExtendedCapabilityFlag implements LongFlag {
        SET_NODE_DESC_SUP((short) 1), INFO_EXT_SUP((short) (1 << 1)), VIRT_SUP((short) (1 << 2)),
        SWITCH_PORT_STATE_TABLE_SUP((short) (1 << 3)), LINK_WIDTH_2X_SUP((short) (1 << 4)), LINK_SPEED_HDR_SUP((short) (1 << 5));

        private final short value;

        ExtendedCapabilityFlag(short value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }
}
