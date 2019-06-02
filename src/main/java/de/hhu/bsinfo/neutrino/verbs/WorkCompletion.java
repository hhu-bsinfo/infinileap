package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.*;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Poolable;

import java.util.Arrays;

@LinkNative("ibv_wc")
public class WorkCompletion extends Struct {

    public enum Status {
        SUCCESS(0), LOC_LEN_ERR(1), LOC_QP_OP_ERR(2), LOC_EEC_OP_ERR(3), LOC_PROT_ERR(4),
        WR_FLUSH_ERR(5), MW_BIND_ERR(6), BAD_RESP_ERR(7), LOC_ACCESS_ERR(8), REM_INV_REQ_ERR(9),
        REM_ACCESS_ERR(10), REM_OP_ERR(11), RETRY_EXC_ERR(12), RNR_RETRY_EXC_ERR(13),
        LOC_RDD_VIOL_ERR(14),REM_INV_RD_REQ_ERR(15), REM_ABORT_ERR(16), INV_EECN_ERR(17),
        INV_EEC_STATE_ERR(18), FATAL_ERR(19), RESP_TIMEOUT_ERR(20), GENERAL_ERR(21), TM_ERR(22),
        TM_RNDV_INCOMPLETE(23);

        private static final Status[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new Status[arrayLength];

            for (Status element : Status.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<Status> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(Status enumeration) {
                return enumeration.value;
            }

            @Override
            public Status toEnum(int integer) {
                if (integer < SUCCESS.value || integer > TM_RNDV_INCOMPLETE.value) {
                    throw new IllegalArgumentException(String.format("Unkown status code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum OpCode {
        SEND(0), RDMA_WRITE(1), RDMA_READ(2), COMP_SWAP(3), FETCH_ADD(4),
        BIND_MW(5), LOCAL_INV(6), TSO(7);

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
                if (integer < SEND.value || integer > TSO.value) {
                    throw new IllegalArgumentException(String.format("Unkown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum WorkCompletionFlag implements Flag {
        GRH(1), IMM(1 << 1), IP_CSUM_OK(1 << 2), WITH_INV(1 << 3), TM_SYNC_REQ(1 << 4),
        TM_MATCH(1 << 5), TM_DATA_INVALID(1 << 6);

        private final int value;

        WorkCompletionFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    private final NativeLong id = longField("wr_id");
    private final NativeEnum<Status> status = enumField("status", Status.CONVERTER);
    private final NativeEnum<OpCode> opCode = enumField("opcode", OpCode.CONVERTER);
    private final NativeInteger vendorError = integerField("vendor_err");
    private final NativeInteger byteCount = integerField("byte_len");
    private final NativeInteger immediateData = integerField("imm_data");
    private final NativeInteger invalidatedRemoteKey = integerField("invalidated_rkey");
    private final NativeInteger queuePairNumber = integerField("qp_num");
    private final NativeInteger sourceQueuePair = integerField("src_qp");
    private final NativeBitMask<WorkCompletionFlag> flags = bitField("wc_flags");
    private final NativeShort partitionKeyIndex = shortField("pkey_index");
    private final NativeShort sourceLocalId = shortField("slid");
    private final NativeByte serviceLevel = byteField("sl");
    private final NativeByte pathBits = byteField("dlid_path_bits");

    public WorkCompletion() {}

    public WorkCompletion(final long handle) {
        super(handle);
    }

    public long getId() {
        return id.get();
    }

    public Status getStatus() {
        return status.get();
    }

    public OpCode getOpCode() {
        return opCode.get();
    }

    public int getVendorError() {
        return vendorError.get();
    }

    public int getByteCount() {
        return byteCount.get();
    }

    public int getImmediateData() {
        return immediateData.get();
    }

    public int getInvalidatedRemoteKey() {
        return invalidatedRemoteKey.get();
    }

    public int getQueuePairNumber() {
        return queuePairNumber.get();
    }

    public int getSourceQueuePair() {
        return sourceQueuePair.get();
    }

    public int getFlags() {
        return flags.get();
    }

    public short getPartitionKeyIndex() {
        return partitionKeyIndex.get();
    }

    public short getSourceLocalId() {
        return sourceLocalId.get();
    }

    public byte getServiceLevel() {
        return serviceLevel.get();
    }

    public byte getPathBits() {
        return pathBits.get();
    }

    @Override
    public String toString() {
        return "{" +
            "\n\tid=" + id +
            ",\n\tstatus=" + status +
            ",\n\topCode=" + opCode +
            ",\n\tvendorError=" + vendorError +
            ",\n\tbyteCount=" + byteCount +
            ",\n\timmediateData=" + immediateData +
            ",\n\tinvalidatedRemoteKey=" + invalidatedRemoteKey +
            ",\n\tqueuePairNumber=" + queuePairNumber +
            ",\n\tsourceQueuePair=" + sourceQueuePair +
            ",\n\tflags=" + flags +
            ",\n\tpartitionKeyIndex=" + partitionKeyIndex +
            ",\n\tsourceLocalId=" + sourceLocalId +
            ",\n\tserviceLevel=" + serviceLevel +
            ",\n\tpathBits=" + pathBits +
            "\n}";
    }

    public static final class TagMatchingInfo extends Struct implements Poolable {

        private final NativeLong tag = longField("tag");
        private final NativeInteger userData = integerField("priv");

        public TagMatchingInfo() {}

        public TagMatchingInfo(final long handle) {
            super(handle);
        }

        public long getTag() {
            return tag.get();
        }

        public int getUserData() {
            return userData.get();
        }
    }
}
