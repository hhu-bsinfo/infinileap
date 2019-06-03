package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeBitMask;
import de.hhu.bsinfo.neutrino.data.NativeBoolean;
import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLinkedList;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.data.NativeShort;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import java.util.Arrays;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_qp")
public class QueuePair extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueuePair.class);

    private final Context context = referenceField("context", Context::new);
    private final NativeLong userContext = longField("qp_context");
    private final ProtectionDomain protectionDomain = referenceField("pd", ProtectionDomain::new);
    private final CompletionQueue sendCompletionQueue = referenceField("send_cq", CompletionQueue::new);
    private final CompletionQueue receiveCompletionQueue = referenceField("recv_cq", CompletionQueue::new);
    private final SharedReceiveQueue sharedReceiveQueue = referenceField("srq", SharedReceiveQueue::new);
    private final NativeInteger queuePairNumber = integerField("qp_num");
    private final NativeEnum<State> state = enumField("state", State.CONVERTER);
    private final NativeEnum<Type> type = enumField("qp_type", Type.CONVERTER);
    private final NativeInteger eventsCompleted = integerField("events_completed");

    QueuePair(long handle) {
        super(handle);
    }

    public boolean postSend(final SendWorkRequest sendWorkRequest) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.postSendWorkRequest(getHandle(), sendWorkRequest.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Posting send work request failed [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public boolean postSend(final NativeLinkedList<SendWorkRequest> sendWorkRequests) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.postSendWorkRequest(getHandle(), sendWorkRequests.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Posting send work request failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public boolean postReceive(final ReceiveWorkRequest receiveWorkRequest) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.postReceiveWorkRequest(getHandle(), receiveWorkRequest.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Posting receive work request failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public boolean postReceive(final NativeLinkedList<ReceiveWorkRequest> receiveWorkRequests) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.postReceiveWorkRequest(getHandle(), receiveWorkRequests.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Posting receive work request failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public boolean modify(final Attributes attributes, final AttributeMask... flags) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.modifyQueuePair(getHandle(), attributes.getHandle(), BitMask.of(flags), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Modifying queue pair failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public Attributes queryAttributes(final AttributeMask... flags) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var attributes = new Attributes();
        var initialAttributes = new InitialAttributes();

        Verbs.queryQueuePair(getHandle(), attributes.getHandle(), BitMask.of(flags), initialAttributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        result.releaseInstance();

        if (isError) {
            LOGGER.error("Querying queue pair failed with error [{}]", result.getStatus());
            return null;
        }

        return attributes;
    }

    public InitialAttributes queryInitialAttributes() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var attributes = new Attributes();
        var initialAttributes = new InitialAttributes();

        Verbs.queryQueuePair(getHandle(), attributes.getHandle(), 0, initialAttributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        result.releaseInstance();

        if (isError) {
            LOGGER.error("Querying queue pair failed with error [{}]", result.getStatus());
            return null;
        }

        return initialAttributes;
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.destroyQueuePair(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Destroying queue pair failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();
    }

    public Context getContext() {
        return context;
    }

    public <T extends NativeObject> T getUserContext(ReferenceFactory<T> factory) {
        return factory.newInstance(userContext.get());
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    public CompletionQueue getSendCompletionQueue() {
        return sendCompletionQueue;
    }

    public CompletionQueue getReceiveCompletionQueue() {
        return receiveCompletionQueue;
    }

    public SharedReceiveQueue getSharedReceiveQueue() {
        return sharedReceiveQueue;
    }

    public int getQueuePairNumber() {
        return queuePairNumber.get();
    }

    public State getState() {
        return state.get();
    }

    public Type getType() {
        return type.get();
    }

    public int getEventsCompleted() {
        return eventsCompleted.get();
    }

    public enum Type {
        RC(2), UC(3), UD(4), RAW_PACKET(8), XRC_SEND(9), XRC_RECV(10), DRIVER(0xFF);

        private static final Type[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new Type[arrayLength];

            for (Type element : Type.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<Type> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(Type enumeration) {
                return enumeration.value;
            }

            @Override
            public Type toEnum(int integer) {
                if (integer < RC.value || integer > UD.value && integer < RAW_PACKET.value
                                       || integer > XRC_RECV.value && integer < DRIVER.value
                                       || integer > DRIVER.value) {
                    throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum TypeFlag implements Flag {
        RC(1 << 2), UC(1 << 3), UD(1 << 4),RAW_PACKET(1 << 8), XRC_SEND(1 << 9), XRC_RECV(1 << 10);

        private final int value;

        TypeFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum State {
        RESET(0), INIT(1), RTR(2), RTS(3), SQD(4), SQE(5), ERR(6), UNKNOWN(7);

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
                if (integer < RESET.value || integer > UNKNOWN.value) {
                    throw new IllegalArgumentException(String.format("Unknown state provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum MigrationState {
        MIGRATED(0), REARM(1), ARMED(2);

        private static final MigrationState[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new MigrationState[arrayLength];

            for (MigrationState element : MigrationState.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        MigrationState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<MigrationState> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(MigrationState enumeration) {
                return enumeration.value;
            }

            @Override
            public MigrationState toEnum(int integer) {
                if (integer < MIGRATED.value || integer > ARMED.value) {
                    throw new IllegalArgumentException(String.format("Unknown migration state provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum AttributeMask implements Flag {
        STATE(1), CUR_STATE(1 << 1), EN_SQD_ASYNC_NOTIFY(1 << 2), ACCESS_FLAGS(1 << 3),
        PKEY_INDEX(1 << 4), PORT(1 << 5), QKEY(1 << 6), AV(1 << 7), PATH_MTU(1 << 8),
        TIMEOUT(1 << 9), RETRY_CNT(1 << 10), RNR_RETRY(1 << 11), RQ_PSN(1 << 12),
        MAX_QP_RD_ATOMIC(1 << 13), ALT_PATH(1 << 14), MIN_RNR_TIMER(1 << 15), SQ_PSN(1 << 16),
        MAX_DEST_RD_ATOMIC(1 << 17), PATH_MIG_STATE(1 << 18), CAP(1 << 19), DEST_QPN(1 << 20),
        RATE_LIMIT(1 << 25);

        private final int value;

        AttributeMask(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    @LinkNative("ibv_qp_init_attr")
    public static final class InitialAttributes extends Struct {

        private final NativeLong userContext = longField("qp_context");
        private final NativeLong sendCompletionQueue = longField("send_cq");
        private final NativeLong receiveCompletionQueue = longField("recv_cq");
        private final NativeLong sharedReceiveQueue = longField("srq");
        private final NativeEnum<Type> type = enumField("qp_type", Type.CONVERTER);
        private final NativeInteger signalLevel = integerField("sq_sig_all");

        public final Capabilities capabilities = valueField("cap", Capabilities::new);

        public InitialAttributes() {}

        public InitialAttributes(Consumer<InitialAttributes> configurator) {
            configurator.accept(this);
        }

        public long getUserContext() {
            return userContext.get();
        }

        public void setUserContext(long userContext) {
            this.userContext.set(userContext);
        }

        public long getSendCompletionQueue() {
            return sendCompletionQueue.get();
        }

        public void setSendCompletionQueue(CompletionQueue sendCompletionQueue) {
            this.sendCompletionQueue.set(sendCompletionQueue.getHandle());
        }

        public long getReceiveCompletionQueue() {
            return receiveCompletionQueue.get();
        }

        public void setReceiveCompletionQueue(CompletionQueue receiveCompletionQueue) {
            this.receiveCompletionQueue.set(receiveCompletionQueue.getHandle());
        }

        public long getSharedReceiveQueue() {
            return sharedReceiveQueue.get();
        }

        public void setSharedReceiveQueue(@Nullable SharedReceiveQueue sharedReceiveQueue) {
            this.sharedReceiveQueue.set(sharedReceiveQueue == null ? 0 : sharedReceiveQueue.getHandle());
        }

        public Type getType() {
            return type.get();
        }

        public void setType(Type type) {
            this.type.set(type);
        }

        public int getSignalLevel() {
            return signalLevel.get();
        }

        public void setSignalLevel(int signalLevel) {
            this.signalLevel.set(signalLevel);
        }

        @Override
        public String toString() {
            return "InitialAttributes {" +
                "\n\tuserContext=" + userContext +
                ",\n\tsendCompletionQueue=" + sendCompletionQueue +
                ",\n\treceiveCompletionQueue=" + receiveCompletionQueue +
                ",\n\tsharedReceiveQueue=" + sharedReceiveQueue +
                ",\n\ttype=" + type +
                ",\n\tsignalLevel=" + signalLevel +
                ",\n\tcapabilities=" + capabilities +
                "\n}";
        }
    }

    @LinkNative("ibv_qp_cap")
    public static final class Capabilities extends Struct {

        private final NativeInteger maxSendWorkRequests = integerField("max_send_wr");
        private final NativeInteger maxReceiveWorkRequests = integerField("max_recv_wr");
        private final NativeInteger maxSendScatterGatherElements = integerField("max_send_sge");
        private final NativeInteger maxReceiveScatterGatherElements = integerField("max_recv_sge");
        private final NativeInteger maxInlineData = integerField("max_inline_data");

        public Capabilities() {
        }

        public Capabilities(LocalBuffer byteBuffer, int offset) {
            super(byteBuffer, offset);
        }

        public int getMaxSendWorkRequests() {
            return maxSendWorkRequests.get();
        }

        public void setMaxSendWorkRequests(int maxSendWorkRequests) {
            this.maxSendWorkRequests.set(maxSendWorkRequests);
        }

        public int getMaxReceiveWorkRequests() {
            return maxReceiveWorkRequests.get();
        }

        public void setMaxReceiveWorkRequests(int maxReceiveWorkRequests) {
            this.maxReceiveWorkRequests.set(maxReceiveWorkRequests);
        }

        public int getMaxSendScatterGatherElements() {
            return maxSendScatterGatherElements.get();
        }

        public void setMaxSendScatterGatherElements(int maxSendScatterGatherElements) {
            this.maxSendScatterGatherElements.set(maxSendScatterGatherElements);
        }

        public int getMaxReceiveScatterGatherElements() {
            return maxReceiveScatterGatherElements.get();
        }

        public void setMaxReceiveScatterGatherElements(int maxReceiveScatterGatherElements) {
            this.maxReceiveScatterGatherElements.set(maxReceiveScatterGatherElements);
        }

        public int getMaxInlineData() {
            return maxInlineData.get();
        }

        public void setMaxInlineData(int maxInlineData) {
            this.maxInlineData.set(maxInlineData);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\tmaxSendWorkRequests=" + maxSendWorkRequests +
                ",\n\tmaxReceiveWorkRequests=" + maxReceiveWorkRequests +
                ",\n\tmaxSendScatterGatherElements=" + maxSendScatterGatherElements +
                ",\n\tmaxReceiveScatterGatherElements=" + maxReceiveScatterGatherElements +
                ",\n\tmaxInlineData=" + maxInlineData +
                "\n}";
        }
    }

    @LinkNative("ibv_qp_attr")
    public static final class Attributes extends Struct {

        private final NativeEnum<State> state = enumField("qp_state", State.CONVERTER);
        private final NativeEnum<State> currentState = enumField("cur_qp_state", State.CONVERTER);
        private final NativeEnum<Mtu> pathMtu = enumField("path_mtu", Mtu.CONVERTER);
        private final NativeEnum<MigrationState> pathMigrationState = enumField("path_mig_state", MigrationState.CONVERTER);
        private final NativeInteger key = integerField("qkey");
        private final NativeInteger receivePacketNumber = integerField("rq_psn");
        private final NativeInteger sendPacketNumber = integerField("sq_psn");
        private final NativeInteger destination = integerField("dest_qp_num");
        private final NativeBitMask<AccessFlag> accessFlags = bitField("qp_access_flags");
        private final NativeShort partitionKeyIndex = shortField("pkey_index");
        private final NativeShort alternatePartitionKeyIndex = shortField("alt_pkey_index");
        private final NativeBoolean notifyDrained = booleanField("en_sqd_async_notify");
        private final NativeBoolean draining = booleanField("sq_draining");
        private final NativeByte maxInitiatorAtomicReads = byteField("max_rd_atomic");
        private final NativeByte maxDestinationAtomicReads = byteField("max_dest_rd_atomic");
        private final NativeByte minRnrTimer = byteField("min_rnr_timer");
        private final NativeByte portNumber = byteField("port_num");
        private final NativeByte timeout = byteField("timeout");
        private final NativeByte retryCount = byteField("retry_cnt");
        private final NativeByte rnrRetryCount = byteField("rnr_retry");
        private final NativeByte altPortNumber = byteField("alt_port_num");
        private final NativeByte altTimeout = byteField("alt_timeout");
        private final NativeInteger rateLimit = integerField("rate_limit");

        public final Capabilities capabilities = valueField("cap", Capabilities::new);
        public final AddressHandle.Attributes addressHandle = valueField("ah_attr", AddressHandle.Attributes::new);
        public final AddressHandle.Attributes alternativeAddressHandle = valueField("alt_ah_attr", AddressHandle.Attributes::new);

        public Attributes() {}

        public Attributes(Consumer<Attributes> configurator) {
            configurator.accept(this);
        }

        public Attributes(final long handle) {
            super(handle);
        }

        public State getState() {
            return state.get();
        }

        public State getCurrentState() {
            return currentState.get();
        }

        public Mtu getPathMtu() {
            return pathMtu.get();
        }

        public MigrationState getPathMigrationState() {
            return pathMigrationState.get();
        }

        public int getKey() {
            return key.get();
        }

        public int getReceivePacketNumber() {
            return receivePacketNumber.get();
        }

        public int getSendPacketNumber() {
            return sendPacketNumber.get();
        }

        public int getDestination() {
            return destination.get();
        }

        public int getAccessFlags() {
            return accessFlags.get();
        }

        public short getPartitionKeyIndex() {
            return partitionKeyIndex.get();
        }

        public short getAlternatePartitionKeyIndex() {
            return alternatePartitionKeyIndex.get();
        }

        public boolean isNotifyDrained() {
            return notifyDrained.get();
        }

        public boolean isDraining() {
            return draining.get();
        }

        public byte getMaxInitiatorAtomicReads() {
            return maxInitiatorAtomicReads.get();
        }

        public byte getMaxDestinationAtomicReads() {
            return maxDestinationAtomicReads.get();
        }

        public byte getMinRnrTimer() {
            return minRnrTimer.get();
        }

        public byte getPortNumber() {
            return portNumber.get();
        }

        public byte getTimeout() {
            return timeout.get();
        }

        public byte getRetryCount() {
            return retryCount.get();
        }

        public byte getRnrRetryCount() {
            return rnrRetryCount.get();
        }

        public byte getAltPortNumber() {
            return altPortNumber.get();
        }

        public byte getAltTimeout() {
            return altTimeout.get();
        }

        public int getRateLimit() {
            return rateLimit.get();
        }

        public void setState(final State value) {
            state.set(value);
        }

        public void setCurrentState(final State value) {
            currentState.set(value);
        }

        public void setPathMtu(final Mtu value) {
            pathMtu.set(value);
        }

        public void setPathMigrationState(final MigrationState value) {
            pathMigrationState.set(value);
        }

        public void setKey(final int value) {
            key.set(value);
        }

        public void setReceivePacketNumber(final int value) {
            receivePacketNumber.set(value);
        }

        public void setSendPacketNumber(final int value) {
            sendPacketNumber.set(value);
        }

        public void setDestination(final int value) {
            destination.set(value);
        }

        public void setAccessFlags(final AccessFlag... flags) {
            accessFlags.set(flags);
        }

        public void setPartitionKeyIndex(final short value) {
            partitionKeyIndex.set(value);
        }

        public void setAlternatePartitionKeyIndex(final short value) {
            alternatePartitionKeyIndex.set(value);
        }

        public void setNotifyDrained(final boolean value) {
            notifyDrained.set(value);
        }

        public void setDraining(final boolean value) {
            draining.set(value);
        }

        public void setMaxInitiatorAtomicReads(final byte value) {
            maxInitiatorAtomicReads.set(value);
        }

        public void setMaxDestinationAtomicReads(final byte value) {
            maxDestinationAtomicReads.set(value);
        }

        public void setMinRnrTimer(final byte value) {
            minRnrTimer.set(value);
        }

        public void setPortNumber(final byte value) {
            portNumber.set(value);
        }

        public void setTimeout(final byte value) {
            timeout.set(value);
        }

        public void setRetryCount(final byte value) {
            retryCount.set(value);
        }

        public void setRnrRetryCount(final byte value) {
            rnrRetryCount.set(value);
        }

        public void setAltPortNumber(final byte value) {
            altPortNumber.set(value);
        }

        public void setAltTimeout(final byte value) {
            altTimeout.set(value);
        }

        public void setRateLimit(final int value) {
            rateLimit.set(value);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\tstate=" + state +
                ",\n\tcurrentState=" + currentState +
                ",\n\tpathMtu=" + pathMtu +
                ",\n\tpathMigrationState=" + pathMigrationState +
                ",\n\tkey=" + key +
                ",\n\treceivedPacketNumber=" + receivePacketNumber +
                ",\n\tsentPacketNumber=" + sendPacketNumber +
                ",\n\tdestination=" + destination +
                ",\n\taccessFlags=" + accessFlags +
                ",\n\tpartitionKeyIndex=" + partitionKeyIndex +
                ",\n\talternatePartitionKeyIndex=" + alternatePartitionKeyIndex +
                ",\n\tnotifyDrained=" + notifyDrained +
                ",\n\tdraining=" + draining +
                ",\n\tmaxInitiatorAtomicReads=" + maxInitiatorAtomicReads +
                ",\n\tmaxDestinationAtomicReads=" + maxDestinationAtomicReads +
                ",\n\tminRnrTimer=" + minRnrTimer +
                ",\n\tportNumber=" + portNumber +
                ",\n\ttimeout=" + timeout +
                ",\n\tretryCount=" + retryCount +
                ",\n\trnrRetryCount=" + rnrRetryCount +
                ",\n\taltPortNumber=" + altPortNumber +
                ",\n\taltTimeout=" + altTimeout +
                ",\n\trateLimit=" + rateLimit +
                ",\n\tcapabilities=" + capabilities +
                ",\n\taddressHandle=" + addressHandle +
                ",\n\talternativeAddressHandle=" + alternativeAddressHandle +
                "\n}";
        }
    }
}
