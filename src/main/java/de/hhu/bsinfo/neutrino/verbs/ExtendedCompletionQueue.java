package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeIntegerBitMask;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import de.hhu.bsinfo.neutrino.verbs.WorkCompletion.TagMatchingInfo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_cq_ex")
public class ExtendedCompletionQueue extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedCompletionQueue.class);

    private final Context context = referenceField("context");
    private final CompletionChannel completionChannel = referenceField("channel");
    private final NativeLong userContextHandle = longField("cq_context");
    private final NativeInteger maxElements = integerField("cqe");
    private final NativeIntegerBitMask<CompatibilityFlag> compatibilityMask = integerBitField("comp_mask");
    private final NativeEnum<WorkCompletion.Status> status = enumField("status", WorkCompletion.Status.CONVERTER);
    private final NativeLong workRequestId = longField("wr_id");

    ExtendedCompletionQueue(final long handle) {
        super(handle);
    }

    @Nullable
    public CompletionQueue toCompletionQueue() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.extendedCompletionQueueToCompletionQueue(getHandle(), result.getHandle());
        if(result.isError()) {
            LOGGER.error("Converting extended completion queue to completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        return result.getAndRelease(CompletionQueue::new);
    }

    public boolean startPolling(final PollAttributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.startPoll(getHandle(), attributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if(isError) {
            LOGGER.error("Starting to poll extended completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        return result.getIntAndRelease() == 0;
    }

    public boolean pollNext() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.nextPoll(getHandle(), result.getHandle());
        boolean isError = result.isError();
        if(isError) {
            LOGGER.error("Polling extended completion queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        return result.getIntAndRelease() == 0;
    }

    public void stopPolling() {
        Verbs.endPoll(getHandle());
    }

    public WorkCompletion.OpCode readOpCode() {
        return WorkCompletion.OpCode.CONVERTER.toEnum(Verbs.readOpCode(getHandle()));
    }

    public int readVendorError() {
        return Verbs.readVendorError(getHandle());
    }

    public int readByteCount() {
        return Verbs.readByteCount(getHandle());
    }

    public int readImmediateData() {
        return Verbs.readImmediateData(getHandle());
    }

    public int readInvalidatedRemoteKey() {
        return Verbs.readInvalidatedRemoteKey(getHandle());
    }

    public int readQueuePairNumber() {
        return Verbs.readQueuePairNumber(getHandle());
    }

    public int readSourceQueuePair() {
        return Verbs.readSourceQueuePair(getHandle());
    }

    public int readWorkCompletionFlags() {
        return Verbs.readWorkCompletionFlags(getHandle());
    }

    public int readSourceLocalId() {
        return Verbs.readSourceLocalId(getHandle());
    }

    public byte readServiceLevel() {
        return Verbs.readServiceLevel(getHandle());
    }

    public byte readPathBits() {
        return Verbs.readPathBits(getHandle());
    }

    public long readCompletionTimestamp() {
        return Verbs.readCompletionTimestamp(getHandle());
    }

    public long readCompletionWallClock() {
        return Verbs.readCompletionWallClockNanoseconds(getHandle());
    }

    public short readCVLan() {
        return Verbs.readCVLan(getHandle());
    }

    public int readFlowTag() {
        return Verbs.readFlowTag(getHandle());
    }

    public TagMatchingInfo readTagMatchingInfo() {
        var ret = new TagMatchingInfo();

        Verbs.readTagMatchingInfo(getHandle(), ret.getHandle());

        return ret;
    }

    @Override
    public void close() {
        NativeObjectRegistry.deregisterObject(this);

        CompletionQueue completionQueue = toCompletionQueue();

        if(completionQueue != null) {
            completionQueue.close();
        }
    }

    public Context getContext() {
        return context;
    }

    public CompletionChannel getCompletionChannel() {
        return completionChannel;
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
                ",\n\tcompletionChannel=" + completionChannel.getHandle() +
                ",\n\tuserContextHandle=" + userContextHandle +
                ",\n\tmaxElements=" + maxElements +
                ",\n\tcompatibilityMask=" + compatibilityMask +
                ",\n\tstatus=" + status +
                ",\n\tworkRequestId=" + workRequestId +
                "\n}";
    }

    public enum WorkCompletionCapability implements Flag {
        WITH_BYTE_LEN(1), WITH_IMM(1 << 1), WITH_QP_NUM(1 << 2), WITH_SRC_QP(1 << 3),
        WITH_SLID(1 << 4), WITH_SL(1 << 5), WITH_DLID_PATH_BITS(1 << 6), WITH_COMPLETION_TIMESTAMP(1 << 7),
        WITH_CVLAN(1 << 8), WITH_FLOW_TAG(1 << 9), WITH_TM_INFO(1 << 10), WITH_COMPLETION_TIMESTAMP_WALLCLOCK(1 << 11);

        private final int value;

        WorkCompletionCapability(int value) {
            this.value = value;
        }

        @Override
        public long getValue() {
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
        public long getValue() {
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
        public long getValue() {
            return value;
        }
    }

    @LinkNative("ibv_cq_init_attr_ex")
    public static final class InitialAttributes extends Struct {

        private final NativeInteger maxElements = integerField("cqe");
        private final NativeLong userContext = longField("cq_context");
        private final NativeLong completionChannel = longField("channel");
        private final NativeInteger completionVector = integerField("comp_vector");
        private final NativeIntegerBitMask<WorkCompletionCapability> workCompletionCapabilities = integerBitField("wc_flags");
        private final NativeIntegerBitMask<CompatibilityFlag> compatibilityMask = integerBitField("comp_mask");
        private final NativeIntegerBitMask<CreationFlag> creationFlags = integerBitField("flags");

        InitialAttributes() {}

        public int getMaxElements() {
            return maxElements.get();
        }

        public long getUserContext() {
            return userContext.get();
        }

        public long getCompletionChannel() {
            return completionChannel.get();
        }

        public int getCompletionVector() {
            return completionVector.get();
        }

        public long getWorkCompletionCapabilities() {
            return workCompletionCapabilities.get();
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public int getCreationFlags() {
            return creationFlags.get();
        }

        public void setMaxElements(final int value) {
            maxElements.set(value);
        }

        public void setUserContext(final long value) {
            userContext.set(value);
        }

        public void setCompletionChannel(final CompletionChannel channel) {
            completionChannel.set(channel.getHandle());
        }

        public void setCompletionVector(final int value) {
            completionVector.set(value);
        }

        public void setWorkCompletionCapabilities(final WorkCompletionCapability... flags) {
            workCompletionCapabilities.set(flags);
        }

        public void setCompatibilityMask(final CompatibilityFlag... flags) {
            compatibilityMask.set(flags);
        }

        public void setCreationFlags(final CreationFlag... flags) {
            this.creationFlags.set(flags);
        }

        @Override
        public String toString() {
            return "InitialAttributes {" +
                    "\n\tmaxElements=" + maxElements +
                    ",\n\tuserContext=" + userContext +
                    ",\n\tcompletionChannel=" + completionChannel +
                    ",\n\tcompletionVector=" + completionVector +
                    ",\n\tworkCompletionFlags=" + workCompletionCapabilities +
                    ",\n\tcompatibilityMask=" + compatibilityMask +
                    ",\n\tflags=" + creationFlags +
                    "\n}";
        }

        public static final class Builder {

            private int maxElements;
            private long userContext = 0;
            private CompletionChannel completionChannel;
            private int completionVector = 0;
            private WorkCompletionCapability[] workCompletionCapabilities;
            private CompatibilityFlag[] compatibilityMask;
            private CreationFlag[] creationFlags;

            public Builder(final int maxElements) {
                this.maxElements = maxElements;
            }

            public Builder withUserContext(final long userContext) {
                this.userContext = userContext;
                return this;
            }

            public Builder withCompletionChannel(final CompletionChannel completionChannel) {
                this.completionChannel = completionChannel;
                return this;
            }

            public Builder withCompletionVector(final int completionVector) {
                this.completionVector = completionVector;
                return this;
            }

            public Builder withWorkCompletionCapabilities(final WorkCompletionCapability... flags) {
                workCompletionCapabilities = flags;
                return this;
            }

            public Builder withCompatibilityMask(final CompatibilityFlag... flags) {
                compatibilityMask = flags;
                return this;
            }

            public Builder withCreationFlags(final CreationFlag... flags) {
                creationFlags = flags;
                return this;
            }

            public InitialAttributes build() {
                var ret = new InitialAttributes();

                ret.setMaxElements(maxElements);
                ret.setUserContext(userContext);
                ret.setCompletionVector(completionVector);

                if(completionChannel != null) ret.setCompletionChannel(completionChannel);
                if(workCompletionCapabilities != null) ret.setWorkCompletionCapabilities(workCompletionCapabilities);
                if(compatibilityMask != null) ret.setCompatibilityMask(compatibilityMask);
                if(creationFlags != null) ret.setCreationFlags(creationFlags);

                return ret;
            }
        }
    }

    @LinkNative("ibv_poll_cq_attr")
    public static final class PollAttributes extends Struct {

        private final NativeInteger compatibilityMask = integerField("comp_mask");

        public PollAttributes() {}

        public PollAttributes(int compatibilityMask) {
            this.compatibilityMask.set(compatibilityMask);
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public void setCompatibilityMask(final int compatibilityMask) {
            this.compatibilityMask.set(compatibilityMask);
        }

        @Override
        public String toString() {
            return "PollAttributes {" +
                    "\n\tcompatibilityMask=" + compatibilityMask +
                    "\n}";
        }
    }
}
