package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeShort;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;
import java.util.Arrays;

public class Port extends Struct {

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
                    throw new IllegalArgumentException(String.format("Unkown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum Mtu {
        IBV_MTU_256(1), IBV_MTU_512(2), IBV_MTU_1024(3), IBV_MTU_2048(4), IBV_MTU_4096(5);

        private static final Mtu[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new Mtu[arrayLength];

            for (Mtu element : Mtu.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        Mtu(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<Mtu> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(Mtu enumeration) {
                return enumeration.value;
            }

            @Override
            public Mtu toEnum(int integer) {
                if (integer < IBV_MTU_256.value || integer > IBV_MTU_4096.value) {
                    throw new IllegalArgumentException(String.format("Unkown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    private static final StructInformation INFO = StructUtil.getInfo("ibv_port_attr");
    private static final int SIZE = INFO.structSize.get();

    private final NativeEnum<State> state = new NativeEnum<>(getByteBuffer(), INFO.getOffset("state"), State.CONVERTER);
    private final NativeEnum<Mtu> maxMtu = new NativeEnum<>(getByteBuffer(), INFO.getOffset("max_mtu"), Mtu.CONVERTER);
    private final NativeEnum<Mtu> activeMtu = new NativeEnum<>(getByteBuffer(), INFO.getOffset("active_mtu"), Mtu.CONVERTER);
    private final NativeInteger gidTableLength = new NativeInteger(getByteBuffer(), INFO.getOffset("gid_tbl_len"));
    private final NativeInteger portCapabilities = new NativeInteger(getByteBuffer(), INFO.getOffset("port_cap_flags"));
    private final NativeInteger maxMessageSize = new NativeInteger(getByteBuffer(), INFO.getOffset("max_msg_sz"));
    private final NativeInteger badPkeyCounter = new NativeInteger(getByteBuffer(), INFO.getOffset("bad_pkey_cntr"));
    private final NativeInteger qkeyViolationCounter = new NativeInteger(getByteBuffer(), INFO.getOffset("qkey_viol_cntr"));
    private final NativeShort pkeyTableLength = new NativeShort(getByteBuffer(), INFO.getOffset("pkey_tbl_len"));
    private final NativeShort localId = new NativeShort(getByteBuffer(), INFO.getOffset("lid"));
    private final NativeShort subnetManagerLocalId = new NativeShort(getByteBuffer(), INFO.getOffset("sm_lid"));
    private final NativeByte localIdMask = new NativeByte(getByteBuffer(), INFO.getOffset("lmc"));
    private final NativeByte maxVirtualLaneCount = new NativeByte(getByteBuffer(), INFO.getOffset("max_vl_num"));
    private final NativeByte subnetManagerServiceLevel = new NativeByte(getByteBuffer(), INFO.getOffset("sm_sl"));
    private final NativeByte subnetTimeout = new NativeByte(getByteBuffer(), INFO.getOffset("subnet_timeout"));
    private final NativeByte initTypeReply = new NativeByte(getByteBuffer(), INFO.getOffset("init_type_reply"));
    private final NativeByte activeWidth = new NativeByte(getByteBuffer(), INFO.getOffset("active_width"));
    private final NativeByte activeSpeed = new NativeByte(getByteBuffer(), INFO.getOffset("active_speed"));
    private final NativeByte physicalState = new NativeByte(getByteBuffer(), INFO.getOffset("phys_state"));
    private final NativeByte linkLayer = new NativeByte(getByteBuffer(), INFO.getOffset("link_layer"));
    private final NativeByte flags = new NativeByte(getByteBuffer(), INFO.getOffset("flags"));
    private final NativeShort portCapabilites2 = new NativeShort(getByteBuffer(), INFO.getOffset("port_cap_flags2"));

    Port() {
        super(SIZE);
    }

    Port(long handle) {
        super(handle, SIZE);
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
        return "Port {" +
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
}
