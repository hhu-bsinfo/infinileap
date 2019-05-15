package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeShort;
import de.hhu.bsinfo.neutrino.data.Struct;
import de.hhu.bsinfo.neutrino.data.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;
import java.util.Arrays;

public class Port extends Struct {

    public enum PortState {
        IBV_PORT_NOP(0), IBV_PORT_DOWN(1), IBV_PORT_INIT(2), IBV_PORT_ARMED(3),
        IBV_PORT_ACTIVE(4), IBV_PORT_ACTIVE_DEFER(5);

        private static final PortState[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new PortState[arrayLength];

            for (PortState element : PortState.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        PortState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PortState valueOf(int state) {
            if (state < IBV_PORT_NOP.value || state > IBV_PORT_ACTIVE_DEFER.value) {
                throw new IllegalArgumentException(String.format("Unkown operation code provided %d", state));
            }

            return VALUES[state];
        }
    }

    public enum Mtu {
        IBV_MTU_256(1), IBV_MTU_512(2), IBV_MTU_1024(3), IBV_MTU_2048(4),
        IBV_MTU_4096(5);

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

        public static Mtu valueOf(int mtu) {
            if (mtu < IBV_MTU_256.value || mtu > IBV_MTU_4096.value) {
                throw new IllegalArgumentException(String.format("Unkown operation code provided %d", mtu));
            }

            return VALUES[mtu];
        }
    }

    private static final StructInformation info = StructUtil.getPortAttributes();

    private static final int SIZE = info.structSize.get();

    private final NativeInteger state = new NativeInteger(getByteBuffer(), info.getOffset("state"));
    private final NativeInteger maxMtu = new NativeInteger(getByteBuffer(), info.getOffset("max_mtu"));
    private final NativeInteger activeMtu = new NativeInteger(getByteBuffer(), info.getOffset("active_mtu"));
    private final NativeInteger gidTableLength = new NativeInteger(getByteBuffer(), info.getOffset("gid_tbl_len"));
    private final NativeInteger portCapabilities = new NativeInteger(getByteBuffer(), info.getOffset("port_cap_flags"));
    private final NativeInteger maxMessageSize = new NativeInteger(getByteBuffer(), info.getOffset("max_msg_sz"));
    private final NativeInteger badPkeyCounter = new NativeInteger(getByteBuffer(), info.getOffset("bad_pkey_cntr"));
    private final NativeInteger qkeyViolationCounter = new NativeInteger(getByteBuffer(), info.getOffset("qkey_viol_cntr"));
    private final NativeShort pkeyTableLength = new NativeShort(getByteBuffer(), info.getOffset("pkey_tbl_len"));
    private final NativeShort localId = new NativeShort(getByteBuffer(), info.getOffset("lid"));
    private final NativeShort subnetManagerLocalId = new NativeShort(getByteBuffer(), info.getOffset("sm_lid"));
    private final NativeByte localIdMask = new NativeByte(getByteBuffer(), info.getOffset("lmc"));
    private final NativeByte maxVirtualLaneCount = new NativeByte(getByteBuffer(), info.getOffset("max_vl_num"));
    private final NativeByte subnetManagerServiceLevel = new NativeByte(getByteBuffer(), info.getOffset("sm_sl"));
    private final NativeByte subnetTimeout = new NativeByte(getByteBuffer(), info.getOffset("subnet_timeout"));
    private final NativeByte initTypeReply = new NativeByte(getByteBuffer(), info.getOffset("init_type_reply"));
    private final NativeByte activeWidth = new NativeByte(getByteBuffer(), info.getOffset("active_width"));
    private final NativeByte activeSpeed = new NativeByte(getByteBuffer(), info.getOffset("active_speed"));
    private final NativeByte physicalState = new NativeByte(getByteBuffer(), info.getOffset("phys_state"));
    private final NativeByte linkLayer = new NativeByte(getByteBuffer(), info.getOffset("link_layer"));
    private final NativeByte flags = new NativeByte(getByteBuffer(), info.getOffset("flags"));
    private final NativeShort portCapabilites2 = new NativeShort(getByteBuffer(), info.getOffset("port_cap_flags2"));

    Port() {
        super(SIZE);
    }

    Port(long handle) {
        super(handle, SIZE);
    }

    public PortState getState() {
        return PortState.valueOf(state.get());
    }

    public Mtu getMaxMtu() {
        return Mtu.valueOf(maxMtu.get());
    }

    public Mtu getActiveMtu() {
        return Mtu.valueOf(activeMtu.get());
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
        return "Port {\n" +
            "\tstate=" + getState() +
            ",\n\tmaxMtu=" + getMaxMtu() +
            ",\n\tactiveMtu=" + getActiveMtu() +
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
