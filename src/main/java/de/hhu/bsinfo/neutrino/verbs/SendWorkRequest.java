package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.*;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.*;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

@LinkNative("ibv_send_wr")
public class SendWorkRequest extends Struct implements Linkable<SendWorkRequest> {

    private final NativeLong id = longField("wr_id");
    private final NativeLong next = longField("next");
    private final NativeLong listHandle = longField("sg_list");
    private final NativeInteger listLength = integerField("num_sge");
    private final NativeEnum<OpCode> opCode = enumField("opcode", OpCode.CONVERTER);
    private final NativeIntegerBitMask<SendFlag> sendFlags = integerBitField("send_flags");
    private final NativeInteger immediateData = integerField("imm_data");
    private final NativeInteger invalidateRemoteKey = integerField("invalidate_rkey");

    public final Rdma rdma = anonymousField(Rdma::new);
    public final Atomic atomic = anonymousField(Atomic::new);
    public final Unreliable ud = anonymousField(Unreliable::new);
    public final ExtendedConnection xrc = anonymousField(ExtendedConnection::new);
    public final BindMemoryWindow bindMemoryWindow = anonymousField(BindMemoryWindow::new);
    public final TcpSegmentOffload tcpSegmentOffload = anonymousField(TcpSegmentOffload::new);

    SendWorkRequest() {}

    public long getId() {
        return id.get();
    }

    public long getListHandle() {
        return listHandle.get();
    }

    public int getListLength() {
        return listLength.get();
    }

    public OpCode getOpCode() {
        return opCode.get();
    }

    public int getSendFlags() {
        return sendFlags.get();
    }

    public int getImmediateData() {
        return immediateData.get();
    }

    public int getInvalidateRemoteKey() {
        return invalidateRemoteKey.get();
    }

    public void setId(final long id) {
        this.id.set(id);
    }

    void setNext(final long next) {
        this.next.set(next);
    }

    public void setScatterGatherElement(final ScatterGatherElement singleSge) {
        listHandle.set(singleSge.getHandle());
        listLength.set(1);
    }

    public void setScatterGatherElement(final ScatterGatherElement.Array list) {
        listHandle.set(list.getHandle());
        listLength.set((int) list.getNativeSize());
    }

    public void setOpCode(final OpCode opCode) {
        this.opCode.set(opCode);
    }

    public void setSendFlags(final SendFlag... sendFlags) {
        this.sendFlags.set(sendFlags);
    }

    public void setImmediateData(final int immediateData) {
        this.immediateData.set(immediateData);
    }

    public void setInvalidateRemoteKey(final int invalidateRemoteKey) {
        this.invalidateRemoteKey.set(invalidateRemoteKey);
    }

    void setListHandle(final long listHandle) {
        this.listHandle.set(listHandle);
    }

    void setListLength(final int listLength) {
        this.listLength.set(listLength);
    }

    @Override
    public void linkWith(final SendWorkRequest other) {
        next.set(other.getHandle());
    }

    @Override
    public void unlink() {
        next.set(0);
    }

    @Override
    public String toString() {
        return "SendWorkRequest {" +
            "\n\tid=" + id +
            ",\n\tnext=" + next +
            ",\n\tlistHandle=" + listHandle +
            ",\n\tlistLength=" + listLength +
            ",\n\topCode=" + opCode +
            ",\n\tflags=" + sendFlags +
            ",\n\timmediateData=" + immediateData +
            ",\n\tinvalidateRemoteKey=" + invalidateRemoteKey +
            ",\n\trdma=" + rdma +
            ",\n\tatomic=" + atomic +
            ",\n\tud=" + ud +
            ",\n\txrc=" + xrc +
            ",\n\tbindMemoryWindow=" + bindMemoryWindow +
            ",\n\ttcpSegmentOffload=" + tcpSegmentOffload +
            "\n}";
    }

    @LinkNative("ibv_send_wr")
    public static final class Rdma extends Struct {

        private final NativeLong remoteAddress = longField("remote_addr");
        private final NativeInteger remoteKey = integerField("rkey");

        Rdma(LocalBuffer buffer) {
            super(buffer, "wr.rdma");
        }

        public long getRemoteAddress() {
            return remoteAddress.get();
        }

        public int getRemoteKey() {
            return remoteKey.get();
        }

        public void setRemoteAddress(final long remoteAddress) {
            this.remoteAddress.set(remoteAddress);
        }

        public void setRemoteKey(final int remoteKey) {
            this.remoteKey.set(remoteKey);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\tremoteAddress=" + remoteAddress +
                ",\n\tremoteKey=" + remoteKey +
                "\n}";
        }
    }

    @LinkNative("ibv_send_wr")
    public static final class Atomic extends Struct {

        private final NativeLong remoteAddress = longField("remote_addr");
        private final NativeLong compareOperand = longField("compare_add");
        private final NativeLong swapOperand = longField("swap");
        private final NativeInteger remoteKey = integerField("rkey");

        Atomic(LocalBuffer buffer) {
            super(buffer, "wr.atomic");
        }

        public long getRemoteAddress() {
            return remoteAddress.get();
        }

        public long getCompareOperand() {
            return compareOperand.get();
        }

        public long getSwapOperand() {
            return swapOperand.get();
        }

        public int getRemoteKey() {
            return remoteKey.get();
        }

        public void setRemoteAddress(final long remoteAddress) {
            this.remoteAddress.set(remoteAddress);
        }

        public void setCompareOperand(final long compareOperand) {
            this.compareOperand.set(compareOperand);
        }

        public void setSwapOperand(final long swapOperand) {
            this.swapOperand.set(swapOperand);
        }

        public void setRemoteKey(final int remoteKey) {
            this.remoteKey.set(remoteKey);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\tremoteAddress=" + remoteAddress +
                ",\n\tcompareOperand=" + compareOperand +
                ",\n\tswapOperand=" + swapOperand +
                ",\n\tremoteKey=" + remoteKey +
                "\n}";
        }
    }

    @LinkNative("ibv_send_wr")
    public static final class Unreliable extends Struct {

        private final NativeLong addressHandle = longField("ah");
        private final NativeInteger remoteQueuePairNumber = integerField("remote_qpn");
        private final NativeInteger remoteQueuePairKey = integerField("remote_qkey");

        Unreliable(LocalBuffer buffer) {
            super(buffer, "wr.ud");
        }

        public AddressHandle getAddressHandle() {
            return NativeObjectRegistry.getObject(addressHandle.get());
        }

        public int getRemoteQueuePairNumber() {
            return remoteQueuePairNumber.get();
        }

        public int getRemoteQueuePairKey() {
            return remoteQueuePairKey.get();
        }

        public void setAddressHandle(final AddressHandle addressHandle) {
            this.addressHandle.set(addressHandle.getHandle());
        }

        public void setRemoteQueuePairNumber(final int remoteQueuePairNumber) {
            this.remoteQueuePairNumber.set(remoteQueuePairNumber);
        }

        public void setRemoteQueuePairKey(final int remoteQueuePairKey) {
            this.remoteQueuePairKey.set(remoteQueuePairKey);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\taddressHandle=" + addressHandle +
                ",\n\tremoteQueuePairNumber=" + remoteQueuePairNumber +
                ",\n\tremoteQueuePairKey=" + remoteQueuePairKey +
                "\n}";
        }
    }

    @LinkNative("ibv_send_wr")
    public static final class ExtendedConnection extends Struct {

        private final NativeInteger remoteSharedReceiveQueueNumber = integerField("remote_srqn");

        ExtendedConnection(LocalBuffer buffer) {
            super(buffer, "qp_type.xrc");
        }

        public int getRemoteSharedReceiveQueueNumber() {
            return remoteSharedReceiveQueueNumber.get();
        }

        public void setRemoteSharedReceiveQueueNumber(final int remoteSharedReceiveQueueNumber) {
            this.remoteSharedReceiveQueueNumber.set(remoteSharedReceiveQueueNumber);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\tremoteSharedReceiveQueueNumber=" + remoteSharedReceiveQueueNumber +
                "\n}";
        }
    }

    @LinkNative("ibv_send_wr")
    public static final class BindMemoryWindow extends Struct {

        private final NativeLong memoryWindow = longField("mw");
        private final NativeInteger remoteKey = integerField("rkey");

        BindMemoryWindow(LocalBuffer buffer) {
            super(buffer, "bind_mw");
        }

        public MemoryWindow.BindInformation bindInformation = valueField("bind_info", MemoryWindow.BindInformation::new);

        public MemoryWindow getMemoryWindow() {
            return NativeObjectRegistry.getObject(memoryWindow.get());
        }

        public int getRemoteKey() {
            return remoteKey.get();
        }

        public void setMemoryWindow(final MemoryWindow memoryWindow) {
            this.memoryWindow.set(memoryWindow.getHandle());
        }

        public void setRemoteKey(final int remoteKey) {
            this.remoteKey.set(remoteKey);
        }

        @Override
        public String toString() {
            return "{" +
                    "\n\tmemoryWindow=" + memoryWindow +
                    ",\n\tremoteKey=" + remoteKey +
                    ",\n\tbindInformation=" + bindInformation +
                    "\n}";
        }
    }

    @LinkNative("ibv_send_wr")
    public static final class TcpSegmentOffload extends Struct {

        private final NativeLong header = longField("hdr");
        private final NativeShort headerSize = shortField("hdr_sz");
        private final NativeShort maxSegmentSize = shortField("mss");

        TcpSegmentOffload(LocalBuffer buffer) {
            super(buffer, "tso");
        }

        public long getHeader() {
            return header.get();
        }

        public short getHeaderSize() {
            return headerSize.get();
        }

        public short getMaxSegmentSize() {
            return maxSegmentSize.get();
        }

        public void setHeader(final long header) {
            this.header.set(header);
        }

        public void setHeaderSize(final short headerSize) {
            this.headerSize.set(headerSize);
        }

        public void setMaxSegmentSize(final short maxSegmentSize) {
            this.maxSegmentSize.set(maxSegmentSize);
        }

        @Override
        public String toString() {
            return "{" +
                    "\n\theader=" + header +
                    ",\n\theaderSize=" + headerSize +
                    ",\n\tmaxSegmentSize=" + maxSegmentSize +
                    "\n}";
        }
    }

    public enum OpCode {
        RDMA_WRITE(0), RDMA_WRITE_WITH_IMM(1), SEND(2), SEND_WITH_IMM(3), RDMA_READ(4),
        ATOMIC_CMP_AND_SWP(5), ATOMIC_FETCH_AND_ADD(6), LOCAL_INV(7), BIND_MW(8),
        SEND_WITH_INV(9), TSO(10);

        private static final OpCode[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new OpCode[arrayLength];

            for (OpCode element : OpCode.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        OpCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<OpCode> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(OpCode enumeration) {
                return enumeration.value;
            }

            @Override
            public OpCode toEnum(int integer) {
                if (integer < RDMA_WRITE.value || integer > TSO.value) {
                    throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum SendFlag implements Flag {
        FENCE(1), SIGNALED(1 << 1), SOLICITED(1 << 2), INLINE(1 << 3), IP_CSUM(1 << 4);

        private final int value;

        SendFlag(int value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    public static class Builder {

        private static final AtomicLong ID_COUNTER = new AtomicLong(0);

        private final long id;
        private final OpCode opCode;
        private long listHandle;
        private int listLength;
        private SendFlag[] sendFlags;
        private int immediateData;
        private int invalidateRemoteKey;
        private int remoteSharedReceiveQueueNumber;

        public Builder(final OpCode opCode) {
            id = ID_COUNTER.getAndIncrement();
            this.opCode = opCode;
        }

        public Builder(final OpCode opCode, final ScatterGatherElement singleSge) {
            id = ID_COUNTER.getAndIncrement();
            this.opCode = opCode;
            listHandle = singleSge.getHandle();
            listLength = 1;
        }

        public Builder(final OpCode opCode, final ScatterGatherElement.Array list) {
            id = ID_COUNTER.getAndIncrement();
            this.opCode = opCode;
            listHandle = list.getHandle();
            listLength = (int) list.getNativeSize();
        }

        public Builder withImmediateData(final int immediateData) {
            this.immediateData = immediateData;
            return this;
        }

        public Builder withInvalidateRemoteKey(final int invalidateRemoteKey) {
            this.invalidateRemoteKey = invalidateRemoteKey;
            return this;
        }

        public Builder withSendFlags(final SendFlag... flags) {
            sendFlags = flags;
            return this;
        }

        public Builder withRemoteSharedReceiveQueueNumber(final int remoteSharedReceiveQueueNumber) {
            this.remoteSharedReceiveQueueNumber = remoteSharedReceiveQueueNumber;
            return this;
        }

        public SendWorkRequest build() {
            var ret = new SendWorkRequest();

            ret.setId(id);
            ret.setListHandle(listHandle);
            ret.setListLength(listLength);
            ret.setOpCode(opCode);
            ret.setImmediateData(immediateData);
            ret.setInvalidateRemoteKey(invalidateRemoteKey);
            ret.xrc.setRemoteSharedReceiveQueueNumber(remoteSharedReceiveQueueNumber);
            if(sendFlags != null) ret.setSendFlags(sendFlags);

            return ret;
        }
    }

    public static final class RdmaBuilder extends Builder {

        private final long remoteAddress;
        private final int remoteKey;

        public RdmaBuilder(final OpCode opCode, final ScatterGatherElement singleSge, final long remoteAddress, final int remoteKey) {
            super(opCode, singleSge);

            if(opCode != OpCode.RDMA_WRITE && opCode != OpCode.RDMA_READ && opCode != OpCode.RDMA_WRITE_WITH_IMM) {
                throw new IllegalArgumentException("Invalid opcode [" + opCode + "] for RDMA operation!");
            }

            this.remoteAddress = remoteAddress;
            this.remoteKey = remoteKey;
        }

        public RdmaBuilder(final OpCode opCode, final ScatterGatherElement.Array list, final long remoteAddress, final int remoteKey) {
            super(opCode, list);

            if(opCode != OpCode.RDMA_WRITE && opCode != OpCode.RDMA_READ && opCode != OpCode.RDMA_WRITE_WITH_IMM) {
                throw new IllegalArgumentException("Invalid opcode [" + opCode + "] for RDMA operation!");
            }

            this.remoteAddress = remoteAddress;
            this.remoteKey = remoteKey;
        }

        @Override
        public SendWorkRequest build() {
            var ret = super.build();

            ret.rdma.setRemoteAddress(remoteAddress);
            ret.rdma.setRemoteKey(remoteKey);

            return ret;
        }
    }

    public static final class AtomicBuilder extends Builder {

        private final long remoteAddress;
        private final int remoteKey;
        private long compareOperand;
        private long swapOperand;

        public AtomicBuilder(final OpCode opCode, final ScatterGatherElement singleSge, final long remoteAddress, final int remoteKey) {
            super(opCode, singleSge);

            if(opCode != OpCode.ATOMIC_CMP_AND_SWP && opCode != OpCode.ATOMIC_FETCH_AND_ADD) {
                throw new IllegalArgumentException("Invalid opcode [" + opCode + "] for ATOMIC operation!");
            }

            this.remoteAddress = remoteAddress;
            this.remoteKey = remoteKey;
        }

        public AtomicBuilder(final OpCode opCode, final ScatterGatherElement.Array list, final long remoteAddress, final int remoteKey) {
            super(opCode, list);

            if(opCode != OpCode.ATOMIC_CMP_AND_SWP && opCode != OpCode.ATOMIC_FETCH_AND_ADD) {
                throw new IllegalArgumentException("Invalid opcode [" + opCode + "] for ATOMIC operation!");
            }

            this.remoteAddress = remoteAddress;
            this.remoteKey = remoteKey;
        }

        public AtomicBuilder withCompareOperand(final long compareOperand) {
            this.compareOperand = compareOperand;
            return this;
        }

        public AtomicBuilder withSwapOperand(final long swapOperand) {
            this.swapOperand = swapOperand;
            return this;
        }

        @Override
        public SendWorkRequest build() {
            var ret = super.build();

            ret.atomic.setRemoteAddress(remoteAddress);
            ret.atomic.setRemoteKey(remoteKey);
            ret.atomic.setCompareOperand(compareOperand);
            ret.atomic.setSwapOperand(swapOperand);

            return ret;
        }
    }

    public static final class UnreliableBuilder extends Builder {

        private final AddressHandle addressHandle;
        private final int remoteQueuePairNumber;
        private final int remoteQueuePairKey;

        public UnreliableBuilder(final OpCode opCode, final ScatterGatherElement singleSge,
                                 final AddressHandle addressHandle, final int remoteQueuePairNumber, final int remoteQueuePairKey) {
            super(opCode, singleSge);

            if(opCode != OpCode.SEND && opCode != OpCode.SEND_WITH_IMM) {
                throw new IllegalArgumentException("Invalid opcode [" + opCode + "] for UD operation!");
            }

            this.addressHandle = addressHandle;
            this.remoteQueuePairNumber = remoteQueuePairNumber;
            this.remoteQueuePairKey = remoteQueuePairKey;
        }

        @Override
        public SendWorkRequest build() {
            var ret = super.build();

            ret.ud.setAddressHandle(addressHandle);
            ret.ud.setRemoteQueuePairNumber(remoteQueuePairNumber);
            ret.ud.setRemoteQueuePairKey(remoteQueuePairKey);

            return ret;
        }
    }

    public static final class BindMemoryWindowBuilder extends Builder {

        private final MemoryWindow memoryWindow;
        private final int remoteKey;

        // Bind Information
        private final MemoryRegion memoryRegion;
        private final long address;
        private final long length;
        private final AccessFlag[] accessFlags;

        public BindMemoryWindowBuilder(final OpCode opCode, final MemoryWindow memoryWindow, final int remoteKey,
                                       final MemoryRegion memoryRegion, final long address, final long length, final AccessFlag... accessFlags) {
            super(opCode);

            if(opCode != OpCode.BIND_MW) {
                throw new IllegalArgumentException("Invalid opcode [" + opCode + "] for BIND MEMORY WINDOW operation!");
            }

            this.memoryWindow = memoryWindow;
            this.remoteKey = remoteKey;

            this.memoryRegion = memoryRegion;
            this.address = address;
            this.length = length;
            this.accessFlags = accessFlags;
        }

        @Override
        public SendWorkRequest build() {
            var ret = super.build();

            ret.bindMemoryWindow.setRemoteKey(remoteKey);
            if(memoryWindow != null) ret.bindMemoryWindow.setMemoryWindow(memoryWindow);

            ret.bindMemoryWindow.bindInformation.setAddress(address);
            ret.bindMemoryWindow.bindInformation.setLength(length);
            if(memoryRegion != null) ret.bindMemoryWindow.bindInformation.setMemoryRegion(memoryRegion);
            if(accessFlags != null) ret.bindMemoryWindow.bindInformation.setAccessFlags(accessFlags);

            return ret;
        }
    }

    public static final class TcpSegmentOffloadBuilder extends Builder {

        private final long header;
        private final short headerSize;
        private final short maxSegmentSize;

        public TcpSegmentOffloadBuilder(final OpCode opCode, final long header, final short headerSize, final short maxSegmentSize) {
            super(opCode);

            if(opCode != OpCode.TSO) {
                throw new IllegalArgumentException("Invalid opcode [" + opCode + "] for TCP SEGMENT OFFLOAD operation!");
            }

            this.header = header;
            this.headerSize = headerSize;
            this.maxSegmentSize = maxSegmentSize;
        }

        @Override
        public SendWorkRequest build() {
            var ret = super.build();

            ret.tcpSegmentOffload.setHeader(header);
            ret.tcpSegmentOffload.setHeaderSize(headerSize);
            ret.tcpSegmentOffload.setMaxSegmentSize(maxSegmentSize);

            return ret;
        }
    }
}
