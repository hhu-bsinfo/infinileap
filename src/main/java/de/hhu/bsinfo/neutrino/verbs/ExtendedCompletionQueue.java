package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeBitMask;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

@LinkNative("ibv_qp_ex")
public class ExtendedCompletionQueue extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedCompletionQueue.class);

    private final Context context = referenceField("context", Context::new);
    private final NativeLong channelHandle = longField("channel");
    private final NativeLong userContextHandle = longField("cq_context");
    private final NativeInteger maxElements = integerField("cqe");
    private final NativeBitMask<CompatibilityFlag> compatibilityMask = bitField("comp_mask");
    private final NativeEnum<WorkCompletion.Status> status = enumField("status", WorkCompletion.Status.CONVERTER);
    private final NativeLong workRequestId = longField("workRequestId");

    public ExtendedCompletionQueue() {}

    public ExtendedCompletionQueue(final long handle) {
        super(handle);
    }

    public CompletionQueue toCompletionQueue() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.extendedCompletionQueueToCompletionQueue(getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Converting extended completion queue to completion queue failed [{}]", result.getStatus());
        }

        return result.getAndRelease(CompletionQueue::new);
    }

    public boolean startPolling(final PollAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.startPoll(getHandle(), attributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if(isError) {
            LOGGER.error("Starting to poll extended completion queue failed with error [{}]", result.getStatus());
        }

        return result.getIntAndRelease() == 0;
    }

    public boolean pollNext() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.nextPoll(getHandle(), result.getHandle());
        boolean isError = result.isError();
        if(isError) {
            LOGGER.error("Polling extended completion queue failed with error [{}]", result.getStatus());
        }

        return result.getIntAndRelease() == 0;
    }

    public void stopPolling() {
        Verbs.endPoll(getHandle());
    }

    public WorkCompletion.OpCode readOpCode() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readOpCode(getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Reading opcode from extended completion queue failed with error [{}]", result.getStatus());
        }

        return WorkCompletion.OpCode.CONVERTER.toEnum(result.getIntAndRelease());
    }

    public int readVendorError() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readVendorError(getHandle(), result.getHandle());

        return result.getIntAndRelease();
    }

    public int readByteCount() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readByteCount(getHandle(), result.getHandle());
        boolean isError = result.isError();

        return result.getIntAndRelease();
    }

    public int readImmediateData() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readImmediateData(getHandle(), result.getHandle());

        return result.getIntAndRelease();
    }

    public int readInvalidatedRemoteKey() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readInvalidatedRemoteKey(getHandle(), result.getHandle());

        return result.getIntAndRelease();
    }

    public int readQueuePairNumber() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readSourceQueuePair(getHandle(), result.getHandle());

        return result.getIntAndRelease();
    }

    public int readSourceQueuePair() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readSourceQueuePair(getHandle(), result.getHandle());

        return result.getIntAndRelease();
    }

    public int readWorkCompletionFlags() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readWorkCompletionFlags(getHandle(), result.getHandle());

        return result.getIntAndRelease();
    }

    public int readSourceLocalId() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readSourceLocalId(getHandle(), result.getHandle());

        return result.getIntAndRelease();
    }

    public byte readServiceLevel() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readServiceLevel(getHandle(), result.getHandle());

        return result.getByteAndRelease();
    }

    public byte readPathBits() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readPathBits(getHandle(), result.getHandle());

        return result.getByteAndRelease();
    }

    public long readCompletionTimestamp() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readCompletionTimestamp(getHandle(), result.getHandle());

        return result.getLongAndRelease();
    }

    public long readCompletionWallClock() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readCompletionWallClockNanoseconds(getHandle(), result.getHandle());

        return result.getLongAndRelease();
    }

    public short readCVLan() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readCVLan(getHandle(), result.getHandle());

        return result.getShortAndRelease();
    }

    public int readFlowTag() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.readFlowTag(getHandle(), result.getHandle());

        return result.getIntAndRelease();
    }

    public void readTagMatchingInfo(WorkCompletion.TagMatchingInfo tagMatchingInfo) {
        Verbs.readTagMatchingInfo(getHandle(), tagMatchingInfo.getHandle());
    }

    public Context getContext() {
        return context;
    }

    public long getChannelHandle() {
        return channelHandle.get();
    }

    public long getUserContextHandle() {
        return userContextHandle.get();
    }

    public int getMaxElements() {
        return maxElements.get();
    }

    public int getCompatibilityMask() {
        return compatibilityMask.get();
    }

    public WorkCompletion.Status getStatus() {
        return status.get();
    }

    public long getWorkRequestId() {
        return workRequestId.get();
    }

    @Override
    public String toString() {
        return "CompletionQueue {" +
                ",\n\tchannelHandle=" + channelHandle +
                ",\n\tuserContextHandle=" + userContextHandle +
                ",\n\tmaxElements=" + maxElements +
                ",\n\tcompatibilityMask=" + compatibilityMask +
                ",\n\tstatus=" + status +
                ",\n\tworkRequestId=" + workRequestId +
                "\n}";
    }

    @Override
    public void close() throws Exception {
        toCompletionQueue().close();
    }

    public enum WorkCompletionFlag implements Flag {
        WITH_BYTE_LEN(1), WITH_IMM(1 << 1), WITH_QP_NUM(1 << 2), WITH_SRC_QP(1 << 3),
        WITH_SLID(1 << 4), WITH_SL(1 << 5), WITH_DLID_PATH_BITS(1 << 6), WITH_COMPLETION_TIMESTAMP(1 << 7),
        WITH_CVLAN(1 << 8), WITH_FLOW_TAG(1 << 9), WITH_TM_INFO(1 << 10), WITH_COMPLETION_TIMESTAMP_WALLCLOCK(1 << 11);

        private final int value;

        WorkCompletionFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum CompatibilityFlag implements Flag {
        FLAGS(1);

        private final int value;

        CompatibilityFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum CreationFlag implements Flag {
        SINGLE_THREADED(1), IGNORE_OVERRUN(1 << 1);

        private final int value;

        CreationFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public static final class InitialAttributes extends Struct {

        private final NativeInteger maxElements = integerField("cqe");
        private final NativeLong userContext = longField("cq_context");
        private final NativeLong channel = longField("channel");
        private final NativeInteger completionVector = integerField("comp_vector");
        private final NativeBitMask<WorkCompletionFlag> workCompletionFlags = bitField("wc_flags");
        private final NativeBitMask<CompatibilityFlag> compatibilityMask = bitField("comp_mask");
        private final NativeBitMask<CreationFlag> flags = bitField("flags");

        public InitialAttributes() {}

        public InitialAttributes(Consumer<InitialAttributes> configurator) {
            configurator.accept(this);
        }

        public InitialAttributes(final long handle) {
            super(handle);
        }

        public int getMaxElements() {
            return maxElements.get();
        }

        public long getUserContext() {
            return userContext.get();
        }

        public long getChannel() {
            return channel.get();
        }

        public int getCompletionVector() {
            return completionVector.get();
        }

        public long getWorkCompletionFlags() {
            return workCompletionFlags.get();
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public int getFlags() {
            return flags.get();
        }

        public void setMaxElements(final int value) {
            maxElements.set(value);
        }

        public void setUserContext(final long value) {
            userContext.set(value);
        }

        public void setChannel(final long value) {
            channel.set(value);
        }

        public void setCompletionVector(final int value) {
            completionVector.set(value);
        }

        public void setWorkCompletionFlags(final WorkCompletionFlag... value) {
            workCompletionFlags.set(value);
        }

        public void setCompatibilityMask(final CompatibilityFlag... value) {
            compatibilityMask.set(value);
        }

        public void setFlags(final CreationFlag... value) {
            flags.set(value);
        }
    }

    public final class PollAttributes extends Struct {

        private final NativeBitMask<CompatibilityFlag> compatibilityMask = bitField("comp_mask");

        public PollAttributes() {}

        public PollAttributes(final long handle) {
            super(handle);
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public void setCompatibilityMask(final CompatibilityFlag... value) {
            compatibilityMask.set(value);
        }
    }
}
