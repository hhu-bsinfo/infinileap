package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeIntegerBitMask;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Linkable;
import de.hhu.bsinfo.neutrino.util.Poolable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@LinkNative("ibv_send_wr")
public class SendWorkRequest extends Struct implements Poolable, Linkable<SendWorkRequest> {

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
        public int getValue() {
            return value;
        }
    }

    private static final AtomicLong ID_COUNTER = new AtomicLong(0);

    private final NativeLong id = longField("wr_id");
    private final NativeLong next = longField("next");
    private final NativeLong listHandle = longField("sg_list");
    private final NativeInteger listLength = integerField("num_sge");
    private final NativeEnum<OpCode> opCode = enumField("opcode", OpCode.CONVERTER);
    private final NativeIntegerBitMask<SendFlag> flags = intBitField("send_flags");
    private final NativeInteger immediateData = integerField("imm_data");
    private final NativeInteger invalidateRemoteKey = integerField("invalidate_rkey");

    public final Rdma rdma = anonymousField(Rdma::new);
    public final Atomic atomic = anonymousField(Atomic::new);
    public final Unreliable ud = anonymousField(Unreliable::new);

    public SendWorkRequest() {
        id.set(ID_COUNTER.getAndIncrement());
    }

    public SendWorkRequest(final Consumer<SendWorkRequest> configurator) {
        configurator.accept(this);
        id.set(ID_COUNTER.getAndIncrement());
    }

    public long getId() {
        return id.get();
    }

    public long getNext() {
        return next.get();
    }

    public void setNext(long next) {
        this.next.set(next);
    }

    public long getListHandle() {
        return listHandle.get();
    }

    public void setListHandle(long listHandle) {
        this.listHandle.set(listHandle);
    }

    public int getListLength() {
        return listLength.get();
    }

    public void setListLength(int listLength) {
        this.listLength.set(listLength);
    }

    public OpCode getOpCode() {
        return opCode.get();
    }

    public void setOpCode(OpCode opCode) {
        this.opCode.set(opCode);
    }

    public int getFlags() {
        return flags.get();
    }

    public void setFlags(SendFlag... flags) {
        this.flags.set(flags);
    }

    public int getImmediateData() {
        return immediateData.get();
    }

    public void setImmediateData(int immediateData) {
        this.immediateData.set(immediateData);
    }

    public int getInvalidateRemoteKey() {
        return invalidateRemoteKey.get();
    }

    public void setInvalidateRemoteKey(int invalidateRemoteKey) {
        this.invalidateRemoteKey.set(invalidateRemoteKey);
    }

    @Override
    public void linkWith(SendWorkRequest other) {
        next.set(other.getHandle());
    }

    @Override
    public String toString() {
        return "SendWorkRequest {" +
            "\n\tid=" + id +
            ",\n\tnext=" + next +
            ",\n\tlistHandle=" + listHandle +
            ",\n\tlistLength=" + listLength +
            ",\n\topCode=" + opCode +
            ",\n\tflags=" + flags +
            ",\n\timmediateData=" + immediateData +
            ",\n\tinvalidateRemoteKey=" + invalidateRemoteKey +
            ",\n\trdma=" + rdma +
            ",\n\tatomic=" + atomic +
            ",\n\tud=" + ud +
            "\n}";
    }

    @LinkNative("ibv_send_wr")
    public static final class Rdma extends Struct {

        private final NativeLong remoteAddress = longField("remote_addr");
        private final NativeInteger remoteKey = integerField("rkey");

        public Rdma(LocalBuffer buffer) {
            super(buffer, "wr.rdma");
        }

        public long getRemoteAddress() {
            return remoteAddress.get();
        }

        public void setRemoteAddress(long remoteAddress) {
            this.remoteAddress.set(remoteAddress);
        }

        public int getRemoteKey() {
            return remoteKey.get();
        }

        public void setRemoteKey(int remoteKey) {
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

        public Atomic(LocalBuffer buffer) {
            super(buffer, "wr.atomic");
        }

        public long getRemoteAddress() {
            return remoteAddress.get();
        }

        public void setRemoteAddress(long remoteAddress) {
            this.remoteAddress.set(remoteAddress);
        }

        public long getCompareOperand() {
            return compareOperand.get();
        }

        public void setCompareOperand(long compareOperand) {
            this.compareOperand.set(compareOperand);
        }

        public long getSwapOperand() {
            return swapOperand.get();
        }

        public void setSwapOperand(long swapOperand) {
            this.swapOperand.set(swapOperand);
        }

        public int getRemoteKey() {
            return remoteKey.get();
        }

        public void setRemoteKey(int remoteKey) {
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

        public Unreliable(LocalBuffer buffer) {
            super(buffer, "wr.ud");
        }

        public long getAddressHandle() {
            return addressHandle.get();
        }

        public void setAddressHandle(long ah) {
            addressHandle.set(ah);
        }

        public int getRemoteQueuePairNumber() {
            return remoteQueuePairNumber.get();
        }

        public void setRemoteQueuePairNumber(int remoteQueuePairNumber) {
            this.remoteQueuePairNumber.set(remoteQueuePairNumber);
        }

        public int getRemoteQueuePairKey() {
            return remoteQueuePairKey.get();
        }

        public void setRemoteQueuePairKey(int remoteQueuePairKey) {
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

}
