package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLinkedList.Linker;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SendWorkRequest extends Struct {

    public enum OpCode {
        RDMA_WRITE(0), RDMA_WRITE_WITH_IMM(1), SEND(2), SEND_WITH_IMM(3), RDMA_READ(4),
        ATOMIC_CMP_AND_SWP(5), ATOMIC_FETCH_AND_ADD(6), LOCAL_INV(7), BIND_MW(5),
        SEND_WITH_INV(6), TSO(7);

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
                    throw new IllegalArgumentException(String.format("Unkown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public static final Linker<SendWorkRequest> LINKER = (current, next) -> {
        current.next.set(next.getHandle());
    };

    private final NativeLong id = longField("wr_id");
    private final NativeLong next = longField("next");
    private final NativeLong listHandle = longField("sg_list");
    private final NativeInteger listLength = integerField("num_sge");
    private final NativeEnum<OpCode> opCode = enumField("opcode", OpCode.CONVERTER, OpCode.RDMA_WRITE);
    private final NativeInteger flags = integerField("send_flags");
    private final NativeInteger immediateData = integerField("imm_data");
    private final NativeInteger invalidateRemoteKey = integerField("invalidate_rkey");

    public final Rdma rdma = anonymousField(Rdma::new);
    public final Atomic atomic = anonymousField(Atomic::new);
    public final Unreliable ud = anonymousField(Unreliable::new);

    public SendWorkRequest() {
        super("ibv_send_wr");
    }

    public SendWorkRequest(final long handle) {
        super("ibv_send_wr", handle);
    }

    public long getId() {
        return id.get();
    }

    public void setId(long id) {
        this.id.set(id);
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

    public void setFlags(int flags) {
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

    public static final class Rdma extends Struct {

        private final NativeLong remoteAddress = longField("remote_addr");
        private final NativeInteger remoteKey = integerField("rkey");

        public Rdma(ByteBuffer buffer) {
            super("ibv_send_wr", "wr.rdma", buffer);
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

    public static final class Atomic extends Struct {

        private final NativeLong remoteAddress = longField("remote_addr");
        private final NativeLong compareOperand = longField("compare_add");
        private final NativeLong swapOperand = longField("swap");
        private final NativeInteger remoteKey = integerField("rkey");

        public Atomic(ByteBuffer buffer) {
            super("ibv_send_wr", "wr.atomic", buffer);
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

    public static final class Unreliable extends Struct {

        private final NativeLong addressHandle = longField("ah");
        private final NativeInteger remoteQueuePairNumber = integerField("remote_qpn");
        private final NativeInteger remoteQueuePairKey = integerField("remote_qkey");

        public Unreliable(ByteBuffer buffer) {
            super("ibv_send_wr", "wr.ud", buffer);
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
