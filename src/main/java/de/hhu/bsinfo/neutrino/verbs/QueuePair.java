package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.EnumConverter;
import de.hhu.bsinfo.neutrino.struct.field.NativeIntegerBitMask;
import de.hhu.bsinfo.neutrino.struct.field.NativeBoolean;
import de.hhu.bsinfo.neutrino.struct.field.NativeByte;
import de.hhu.bsinfo.neutrino.struct.field.NativeEnum;
import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.field.NativeLinkedList;
import de.hhu.bsinfo.neutrino.struct.field.NativeLong;
import de.hhu.bsinfo.neutrino.struct.field.NativeObject;
import de.hhu.bsinfo.neutrino.struct.field.NativeShort;
import de.hhu.bsinfo.neutrino.struct.LinkNative;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import de.hhu.bsinfo.neutrino.util.factory.ReferenceFactory;
import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import org.agrona.concurrent.AtomicBuffer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_qp")
public class QueuePair extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueuePair.class);

    private final Context context = referenceField("context");
    private final NativeLong userContext = longField("qp_context");
    private final ProtectionDomain protectionDomain = referenceField("pd");
    private final CompletionQueue sendCompletionQueue = referenceField("send_cq");
    private final CompletionQueue receiveCompletionQueue = referenceField("recv_cq");
    private final SharedReceiveQueue sharedReceiveQueue = referenceField("srq");
    private final NativeInteger queuePairNumber = integerField("qp_num");
    private final NativeEnum<State> state = enumField("state", State.CONVERTER);
    private final NativeEnum<Type> type = enumField("qp_type", Type.CONVERTER);
    private final NativeInteger eventsCompleted = integerField("events_completed");

    QueuePair(final long handle) {
        super(handle);
    }

    QueuePair(final AtomicBuffer buffer, final int offset) {
        super(buffer, offset);
    }

    private void postSend(final long sendWorkRequestsHandle) throws IOException {
        var result = Result.localInstance();

        Verbs.postSendWorkRequestQueuePair(getHandle(), sendWorkRequestsHandle, result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            throw new IOException(SystemUtil.getErrorMessage());
        }
    }

    private void postReceive(final long receiveWorkRequestsHandle) throws IOException {
        var result = Result.localInstance();

        Verbs.postReceiveWorkRequestQueuePair(getHandle(), receiveWorkRequestsHandle, result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            throw new IOException(SystemUtil.getErrorMessage());
        }
    }

    public ExtendedQueuePair toExtendedQueuePair() throws IOException {
        var result = Result.localInstance();

        Verbs.queuePairToExtendedQueuePair(getHandle(), result.getHandle());
        if(result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        return result.get(ExtendedQueuePair::new);
    }

    public void postSend(final SendWorkRequest sendWorkRequest) throws IOException {
        postSend(sendWorkRequest.getHandle());
    }

    public void postSend(final NativeLinkedList<SendWorkRequest> sendWorkRequests) throws IOException {
        postSend(sendWorkRequests.getHandle());
    }

    public void postReceive(final ReceiveWorkRequest receiveWorkRequest) throws IOException {
        postReceive(receiveWorkRequest.getHandle());
    }

    public void postReceive(final NativeLinkedList<ReceiveWorkRequest> receiveWorkRequests) throws IOException {
        postReceive(receiveWorkRequests.getHandle());
    }

    private void modify(final Attributes attributes, final AttributeFlag... flags) throws IOException {
        var result = Result.localInstance();

        Verbs.modifyQueuePair(getHandle(), attributes.getHandle(), BitMask.intOf(flags), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            throw new IOException(SystemUtil.getErrorMessage());
        }
    }

    public void modify(final Attributes.Builder builder) throws IOException {
        modify(builder.build(), builder.getAttributeFlags());
    }

    @Nullable
    public Attributes queryAttributes(final AttributeFlag... flags) {
        var result = Result.localInstance();
        var attributes = new Attributes();
        var initialAttributes = new InitialAttributes();

        Verbs.queryQueuePair(getHandle(), attributes.getHandle(), BitMask.intOf(flags), initialAttributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Querying queue pair failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            attributes = null;
        }



        return attributes;
    }

    @Nullable
    public InitialAttributes queryInitialAttributes() {
        var result = Result.localInstance();
        var attributes = new Attributes();
        var initialAttributes = new InitialAttributes();

        Verbs.queryQueuePair(getHandle(), attributes.getHandle(), 0, initialAttributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Querying queue pair failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
            initialAttributes = null;
        }



        return initialAttributes;
    }

    @Override
    public void close() throws IOException {
        var result = Result.localInstance();

        Verbs.destroyQueuePair(getHandle(), result.getHandle());
        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        NativeObjectRegistry.deregisterObject(this);
    }

    @Override
    public String toString() {
        return "QueuePair {" +
                "\n\tuserContext=" + userContext +
                ",\n\tsendCompletionQueue=" + sendCompletionQueue +
                ",\n\treceiveCompletionQueue=" + receiveCompletionQueue +
                ",\n\tsharedReceiveQueue=" + sharedReceiveQueue +
                ",\n\tqueuePairNumber=" + queuePairNumber +
                ",\n\tstate=" + state +
                ",\n\ttype=" + type +
                ",\n\teventsCompleted=" + eventsCompleted +
                "\n}";
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

    public enum TypeFlag implements IntegerFlag {
        RC(1 << 2), UC(1 << 3), UD(1 << 4), RAW_PACKET(1 << 8), XRC_SEND(1 << 9), XRC_RECV(1 << 10);

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

    public enum AttributeFlag implements IntegerFlag {
        STATE(1), CUR_STATE(1 << 1), EN_SQD_ASYNC_NOTIFY(1 << 2), ACCESS_FLAGS(1 << 3),
        PKEY_INDEX(1 << 4), PORT(1 << 5), QKEY(1 << 6), AV(1 << 7), PATH_MTU(1 << 8),
        TIMEOUT(1 << 9), RETRY_CNT(1 << 10), RNR_RETRY(1 << 11), RQ_PSN(1 << 12),
        MAX_QP_RD_ATOMIC(1 << 13), ALT_PATH(1 << 14), MIN_RNR_TIMER(1 << 15), SQ_PSN(1 << 16),
        MAX_DEST_RD_ATOMIC(1 << 17), PATH_MIG_STATE(1 << 18), CAP(1 << 19), DEST_QPN(1 << 20),
        RATE_LIMIT(1 << 25);

        private final int value;

        AttributeFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum OpenAttributeFlag implements IntegerFlag {
        NUM(1), XRCD(1 << 1), CONTEXT(1 << 2), TYPE(1 << 3), RESERVED(1 << 4);

        private final int value;

        OpenAttributeFlag(int value) {
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

        public InitialAttributes(final Consumer<InitialAttributes> configurator) {
            configurator.accept(this);
        }

        public long getUserContext() {
            return userContext.get();
        }

        public CompletionQueue getSendCompletionQueue() {
            return NativeObjectRegistry.getObject(sendCompletionQueue.get());
        }

        public CompletionQueue getReceiveCompletionQueue() {
            return NativeObjectRegistry.getObject(receiveCompletionQueue.get());
        }

        public SharedReceiveQueue getSharedReceiveQueue() {
            return NativeObjectRegistry.getObject(sharedReceiveQueue.get());
        }

        public Type getType() {
            return type.get();
        }

        public int getSignalLevel() {
            return signalLevel.get();
        }

        public void setUserContext(long userContext) {
            this.userContext.set(userContext);
        }

        public void setSendCompletionQueue(CompletionQueue sendCompletionQueue) {
            this.sendCompletionQueue.set(sendCompletionQueue.getHandle());
        }


        public void setReceiveCompletionQueue(CompletionQueue receiveCompletionQueue) {
            this.receiveCompletionQueue.set(receiveCompletionQueue.getHandle());
        }

        public void setSharedReceiveQueue(@Nullable SharedReceiveQueue sharedReceiveQueue) {
            this.sharedReceiveQueue.set(sharedReceiveQueue == null ? 0 : sharedReceiveQueue.getHandle());
        }

        public void setType(Type type) {
            this.type.set(type);
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

        public static final class Builder {

            private long userContext;
            private CompletionQueue sendCompletionQueue;
            private CompletionQueue receiveCompletionQueue;
            private SharedReceiveQueue sharedReceiveQueue;
            private QueuePair.Type type;
            private int signalLevel;

            // Capabilities
            private int maxSendWorkRequests;
            private int maxReceiveWorkRequests;
            private int maxSendScatterGatherElements;
            private int maxReceiveScatterGatherElements;
            private int maxInlineData;

            public Builder(Type type, CompletionQueue sendCompletionQueue, CompletionQueue receiveCompletionQueue,
                           int maxSendWorkRequests, int maxReceiveWorkRequests, int maxSendScatterGatherElements, int maxReceiveScatterGatherElements) {
                this.type = type;
                this.sendCompletionQueue = sendCompletionQueue;
                this.receiveCompletionQueue = receiveCompletionQueue;
                this.maxSendWorkRequests = maxSendWorkRequests;
                this.maxReceiveWorkRequests = maxReceiveWorkRequests;
                this.maxSendScatterGatherElements = maxSendScatterGatherElements;
                this.maxReceiveScatterGatherElements = maxReceiveScatterGatherElements;
            }

            public Builder withUserContext(final long userContext) {
                this.userContext = userContext;
                return this;
            }

            public Builder withSharedReceiveQueue(final SharedReceiveQueue sharedReceiveQueue) {
                this.sharedReceiveQueue = sharedReceiveQueue;
                return this;
            }

            public Builder withType(final QueuePair.Type type) {
                this.type = type;
                return this;
            }

            public Builder withSignalLevel(final int signalLevel) {
                this.signalLevel = signalLevel;
                return this;
            }

            public Builder withMaxInlineData(final int maxInlineData) {
                this.maxInlineData = maxInlineData;
                return this;
            }

            public InitialAttributes build() {
                var ret = new InitialAttributes();

                ret.setType(type);
                ret.setUserContext(userContext);
                ret.setSignalLevel(signalLevel);

                if(sendCompletionQueue != null) ret.setSendCompletionQueue(sendCompletionQueue);
                if(receiveCompletionQueue != null) ret.setReceiveCompletionQueue(receiveCompletionQueue);
                if(sharedReceiveQueue != null) ret.setSharedReceiveQueue(sharedReceiveQueue);

                ret.capabilities.setMaxSendWorkRequests(maxSendWorkRequests);
                ret.capabilities.setMaxReceiveWorkRequests(maxReceiveWorkRequests);
                ret.capabilities.setMaxSendScatterGatherElements(maxSendScatterGatherElements);
                ret.capabilities.setMaxReceiveScatterGatherElements(maxReceiveScatterGatherElements);
                ret.capabilities.setMaxInlineData(maxInlineData);

                return ret;
            }
        }
    }

    @LinkNative("ibv_qp_cap")
    public static final class Capabilities extends Struct {

        private final NativeInteger maxSendWorkRequests = integerField("max_send_wr");
        private final NativeInteger maxReceiveWorkRequests = integerField("max_recv_wr");
        private final NativeInteger maxSendScatterGatherElements = integerField("max_send_sge");
        private final NativeInteger maxReceiveScatterGatherElements = integerField("max_recv_sge");
        private final NativeInteger maxInlineData = integerField("max_inline_data");

        Capabilities(AtomicBuffer byteBuffer, int offset) {
            super(byteBuffer, offset);
        }

        public int getMaxReceiveWorkRequests() {
            return maxReceiveWorkRequests.get();
        }

        public int getMaxSendWorkRequests() {
            return maxSendWorkRequests.get();
        }

        public int getMaxSendScatterGatherElements() {
            return maxSendScatterGatherElements.get();
        }

        public int getMaxReceiveScatterGatherElements() {
            return maxReceiveScatterGatherElements.get();
        }

        public int getMaxInlineData() {
            return maxInlineData.get();
        }

        public void setMaxReceiveWorkRequests(int maxReceiveWorkRequests) {
            this.maxReceiveWorkRequests.set(maxReceiveWorkRequests);
        }

        public void setMaxSendWorkRequests(int maxSendWorkRequests) {
            this.maxSendWorkRequests.set(maxSendWorkRequests);
        }

        public void setMaxSendScatterGatherElements(int maxSendScatterGatherElements) {
            this.maxSendScatterGatherElements.set(maxSendScatterGatherElements);
        }

        public void setMaxReceiveScatterGatherElements(int maxReceiveScatterGatherElements) {
            this.maxReceiveScatterGatherElements.set(maxReceiveScatterGatherElements);
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

    @LinkNative("ibv_qp_open_attr")
    public static final class OpenAttributes extends Struct {

        private final NativeIntegerBitMask<OpenAttributeFlag> attributeMask = integerBitField("comp_mask");
        private final NativeInteger queuePairNumber = integerField("qp_num");
        private final NativeLong extendedConnectionDomain = longField("xrcd");
        private final NativeLong userContext = longField("qp_context");
        private final NativeEnum<Type> type = enumField("qp_type", Type.CONVERTER);

        OpenAttributes() {}

        public int getAttributeMask() {
            return attributeMask.get();
        }

        public int getQueuePairNumber() {
            return queuePairNumber.get();
        }

        public ExtendedConnectionDomain getExtendedConnectionDomain() {
            return NativeObjectRegistry.getObject(extendedConnectionDomain.get());
        }

        public long getUserContext() {
            return userContext.get();
        }

        public Type getType() {
            return type.get();
        }

        void setAttributeMask(final OpenAttributeFlag... flags) {
            attributeMask.set(flags);
        }

        void setQueuePairNumber(final int value) {
            queuePairNumber.set(value);
        }

        void setExtendedConnectionDomain(final ExtendedConnectionDomain extendedConnectionDomain) {
            this.extendedConnectionDomain.set(extendedConnectionDomain.getHandle());
        }

        void setUserContext(final long value) {
            userContext.set(value);
        }

        void setType(final Type value) {
            type.set(value);
        }

        @Override
        public String toString() {
            return "OpenAttributes {" +
                    "\n\tattributeMask=" + attributeMask +
                    ",\n\tqueuePairNumber=" + queuePairNumber +
                    ",\n\textendedConnectionDomain=" + extendedConnectionDomain +
                    ",\n\tuserContext=" + userContext +
                    ",\n\ttype=" + type +
                    "\n}";
        }

        public static final class Builder {

            private final Set<OpenAttributeFlag> attributeFlags = new HashSet<>();
            private int queuePairNumber;
            private ExtendedConnectionDomain extendedConnectionDomain;
            private long userContext;
            private Type type;

            public Builder(int queuePairNumber, Type type, ExtendedConnectionDomain extendedConnectionDomain) {
                this.type = type;
                this.queuePairNumber = queuePairNumber;
                this.extendedConnectionDomain = extendedConnectionDomain;
                attributeFlags.add(OpenAttributeFlag.XRCD);
                attributeFlags.add(OpenAttributeFlag.TYPE);
                attributeFlags.add(OpenAttributeFlag.NUM);
            }

            public Builder withUserContext(long userContext) {
                this.userContext = userContext;
                return this;
            }

            public OpenAttributes build() {
                var ret = new OpenAttributes();

                ret.setAttributeMask(attributeFlags.toArray(new OpenAttributeFlag[0]));
                ret.setQueuePairNumber(queuePairNumber);
                ret.setUserContext(userContext);
                ret.setType(type);
                ret.setExtendedConnectionDomain(extendedConnectionDomain);

                return ret;
            }
        }
    }

    @LinkNative("ibv_qp_attr")
    public static final class Attributes extends Struct {

        private final NativeEnum<State> state = enumField("qp_state", State.CONVERTER);
        private final NativeEnum<State> currentState = enumField("cur_qp_state", State.CONVERTER);
        private final NativeEnum<Mtu> pathMtu = enumField("path_mtu", Mtu.CONVERTER);
        private final NativeEnum<MigrationState> pathMigrationState = enumField("path_mig_state", MigrationState.CONVERTER);
        private final NativeInteger qkey = integerField("qkey");
        private final NativeInteger receivePacketNumber = integerField("rq_psn");
        private final NativeInteger sendPacketNumber = integerField("sq_psn");
        private final NativeInteger remoteQueuePairNumber = integerField("dest_qp_num");
        private final NativeIntegerBitMask<AccessFlag> accessFlags = integerBitField("qp_access_flags");
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
        private final NativeByte alternatePortNumber = byteField("alt_port_num");
        private final NativeByte alternateTimeout = byteField("alt_timeout");
        private final NativeInteger rateLimit = integerField("rate_limit");

        public final Capabilities capabilities = valueField("cap", Capabilities::new);
        public final AddressHandle.Attributes addressHandle = valueField("ah_attr", AddressHandle.Attributes::new);
        public final AddressHandle.Attributes alternateAddressHandle = valueField("alt_ah_attr", AddressHandle.Attributes::new);

        Attributes() {}

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

        public int getQkey() {
            return qkey.get();
        }

        public int getReceivePacketNumber() {
            return receivePacketNumber.get();
        }

        public int getSendPacketNumber() {
            return sendPacketNumber.get();
        }

        public int getRemoteQueuePairNumber() {
            return remoteQueuePairNumber.get();
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

        public byte getAlternatePortNumber() {
            return alternatePortNumber.get();
        }

        public byte getAlternateTimeout() {
            return alternateTimeout.get();
        }

        public int getRateLimit() {
            return rateLimit.get();
        }

        void setState(final State value) {
            state.set(value);
        }

        void setCurrentState(final State value) {
            currentState.set(value);
        }

        void setPathMtu(final Mtu value) {
            pathMtu.set(value);
        }

        void setPathMigrationState(final MigrationState value) {
            pathMigrationState.set(value);
        }

        void setQkey(final int value) {
            qkey.set(value);
        }

        void setReceivePacketNumber(final int value) {
            receivePacketNumber.set(value);
        }

        void setSendPacketNumber(final int value) {
            sendPacketNumber.set(value);
        }

        void setRemoteQueuePairNumber(final int value) {
            remoteQueuePairNumber.set(value);
        }

        void setAccessFlags(final AccessFlag... flags) {
            accessFlags.set(flags);
        }

        void setPartitionKeyIndex(final short value) {
            partitionKeyIndex.set(value);
        }

        void setAlternatePartitionKeyIndex(final short value) {
            alternatePartitionKeyIndex.set(value);
        }

        void setNotifyDrained(final boolean value) {
            notifyDrained.set(value);
        }

        void setDraining(final boolean value) {
            draining.set(value);
        }

        void setMaxInitiatorAtomicReads(final byte value) {
            maxInitiatorAtomicReads.set(value);
        }

        void setMaxDestinationAtomicReads(final byte value) {
            maxDestinationAtomicReads.set(value);
        }

        void setMinRnrTimer(final byte value) {
            minRnrTimer.set(value);
        }

        void setPortNumber(final byte value) {
            portNumber.set(value);
        }

        void setTimeout(final byte value) {
            timeout.set(value);
        }

        void setRetryCount(final byte value) {
            retryCount.set(value);
        }

        void setRnrRetryCount(final byte value) {
            rnrRetryCount.set(value);
        }

        void setAlternatePortNumber(final byte value) {
            alternatePortNumber.set(value);
        }

        void setAlternateTimeout(final byte value) {
            alternateTimeout.set(value);
        }

        void setRateLimit(final int value) {
            rateLimit.set(value);
        }

        @Override
        public String toString() {
            return "{" +
                "\n\tstate=" + state +
                ",\n\tcurrentState=" + currentState +
                ",\n\tpathMtu=" + pathMtu +
                ",\n\tpathMigrationState=" + pathMigrationState +
                ",\n\tkey=" + qkey +
                ",\n\treceivedPacketNumber=" + receivePacketNumber +
                ",\n\tsentPacketNumber=" + sendPacketNumber +
                ",\n\tdestination=" + remoteQueuePairNumber +
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
                ",\n\taltPortNumber=" + alternatePortNumber +
                ",\n\taltTimeout=" + alternateTimeout +
                ",\n\trateLimit=" + rateLimit +
                ",\n\tcapabilities=" + capabilities +
                ",\n\taddressHandle=" + addressHandle +
                ",\n\talternativeAddressHandle=" + alternateAddressHandle +
                "\n}";
        }

        public static final class Builder {

            private State state;
            private State currentState;
            private Mtu pathMtu;
            private MigrationState pathMigrationState;
            private int qkey;
            private int receivePacketNumber;
            private int sendPacketNumber;
            private int remoteQueuePairNumber;
            private AccessFlag[] accessFlags;
            private short partitionKeyIndex;
            private short alternatePartitionKeyIndex;
            private boolean notifyDrained;
            private byte maxInitiatorAtomicReads;
            private byte maxDestinationAtomicReads;
            private byte minRnrTimer;
            private byte portNumber;
            private byte timeout;
            private byte retryCount;
            private byte rnrRetryCount;
            private byte alternatePortNumber;
            private byte alternateTimeout;
            private int rateLimit;

            // Capabilities
            private int maxSendWorkRequests;
            private int maxReceiveWorkRequests;
            private int maxSendScatterGatherElements;
            private int maxReceiveScatterGatherElements;
            private int maxInlineData;

            // Address Handle
            private short remoteLocalId;
            private byte serviceLevel;
            private byte sourcePathBits;
            private byte staticRate;
            private boolean isGlobal;
            private byte remotePortNumber;
            private long remoteGlobalId;
            private int flowLabel;
            private byte index;
            private byte hopLimit;
            private byte trafficClass;

            // Alternate Address Handle
            private short alternateRemoteLocalId;
            private byte alternateServiceLevel;
            private byte alternateSourcePathBits;
            private byte alternateStaticRate;
            private boolean alternateIsGlobal;
            private byte alternateRemotePortNumber;
            private long alternateRemoteGlobalId;
            private int alternateFlowLabel;
            private byte alternateIndex;
            private byte alternateHopLimit;
            private byte alternateTrafficClass;

            private final Set<AttributeFlag> attributeFlags = new HashSet<>();

            public Builder() {}

            public static Builder buildInitAttributesRC(final short partitionKeyIndex, final byte portNumber, final AccessFlag... accessFlags) {
                return new Builder()
                        .withState(State.INIT)
                        .withPartitionKeyIndex(partitionKeyIndex)
                        .withPortNumber(portNumber)
                        .withAccessFlags(accessFlags);
            }

            public static Builder buildInitAttributesUD(final short partitionKeyIndex, final byte portNumber) {
                return new Builder()
                        .withState(State.INIT)
                        .withPartitionKeyIndex(partitionKeyIndex)
                        .withPortNumber(portNumber)
                        // Default values
                        .withQkey(0x222222);
            }

            public static Builder buildReadyToReceiveAttributesRC(final int remoteQueuePairNumber, final short remoteLocalId, final byte remotePortNumber) {
                return new Builder()
                        .withState(State.RTR)
                        .withRemoteQueuePairNumber(remoteQueuePairNumber)
                        .withRemoteLocalId(remoteLocalId)
                        .withRemotePortNumber(remotePortNumber)
                        // Default values
                        .withPathMtu(Mtu.MTU_4096)
                        .withReceivePacketNumber(0)
                        .withMaxDestinationAtomicReads((byte) 1)
                        .withMinRnrTimer((byte) 12)
                        .withIsGlobal(false)
                        .withServiceLevel((byte) 1)
                        .withSourcePathBits((byte) 0);
            }

            public static Builder buildReadyToReceiveAttributesUD() {
                return new Builder()
                        .withState(State.RTR);
            }

            public static Builder buildReadyToSendAttributesRC() {
                return new Builder()
                        .withState(State.RTS)
                        // Default values
                        .withSendPacketNumber(0)
                        .withTimeout((byte) 14)
                        .withRetryCount((byte) 7)
                        .withRnrRetryCount((byte) 7)
                        .withMaxInitiatorAtomicReads((byte) 1);
            }

            public static Builder buildReadyToSendAttributesUD() {
                return new Builder()
                        .withState(State.RTS)
                        // Default values
                        .withSendPacketNumber(0);
            }

            public Builder withState(final State state) {
                this.state = state;
                attributeFlags.add(AttributeFlag.STATE);
                return this;
            }

            public Builder withCurrentState(final State currentState) {
                this.currentState = currentState;
                attributeFlags.add(AttributeFlag.CUR_STATE);
                return this;
            }

            public Builder withPathMtu(final Mtu pathMtu) {
                this.pathMtu = pathMtu;
                attributeFlags.add(AttributeFlag.PATH_MTU);
                return this;
            }

            public Builder withPathMigrationState(final MigrationState pathMigrationState) {
                this.pathMigrationState = pathMigrationState;
                attributeFlags.add(AttributeFlag.PATH_MIG_STATE);
                return this;
            }

            public Builder withQkey(final int qkey) {
                this.qkey = qkey;
                attributeFlags.add(AttributeFlag.QKEY);
                return this;
            }

            public Builder withReceivePacketNumber(final int receivePacketNumber) {
                this.receivePacketNumber = receivePacketNumber;
                attributeFlags.add(AttributeFlag.RQ_PSN);
                return this;
            }

            public Builder withSendPacketNumber(final int sendPacketNumber) {
                this.sendPacketNumber = sendPacketNumber;
                attributeFlags.add(AttributeFlag.SQ_PSN);
                return this;
            }

            public Builder withRemoteQueuePairNumber(final int remoteQueuePairNumber) {
                this.remoteQueuePairNumber = remoteQueuePairNumber;
                attributeFlags.add(AttributeFlag.DEST_QPN);
                return this;
            }

            public Builder withAccessFlags(final AccessFlag... flags) {
                accessFlags = flags;
                attributeFlags.add(AttributeFlag.ACCESS_FLAGS);
                return this;
            }

            public Builder withPartitionKeyIndex(final short partitionKeyIndex) {
                this.partitionKeyIndex = partitionKeyIndex;
                attributeFlags.add(AttributeFlag.PKEY_INDEX);
                return this;
            }

            public Builder withAlternatePartitionKeyIndex(final short alternatePartitionKeyIndex) {
                this.alternatePartitionKeyIndex = alternatePartitionKeyIndex;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withNotifyDrained(final boolean notifyDrained) {
                this.notifyDrained = notifyDrained;
                attributeFlags.add(AttributeFlag.EN_SQD_ASYNC_NOTIFY);
                return this;
            }

            public Builder withMaxInitiatorAtomicReads(final byte maxInitiatorAtomicReads) {
                this.maxInitiatorAtomicReads = maxInitiatorAtomicReads;
                attributeFlags.add(AttributeFlag.MAX_QP_RD_ATOMIC);
                return this;
            }

            public Builder withMaxDestinationAtomicReads(final byte maxDestinationAtomicReads) {
                this.maxDestinationAtomicReads = maxDestinationAtomicReads;
                attributeFlags.add(AttributeFlag.MAX_DEST_RD_ATOMIC);
                return this;
            }

            public Builder withMinRnrTimer(final byte minRnrTimer) {
                this.minRnrTimer = minRnrTimer;
                attributeFlags.add(AttributeFlag.MIN_RNR_TIMER);
                return this;
            }

            public Builder withPortNumber(final byte portNumber) {
                this.portNumber = portNumber;
                attributeFlags.add(AttributeFlag.PORT);
                return this;
            }

            public Builder withTimeout(final byte timeout) {
                this.timeout = timeout;
                attributeFlags.add(AttributeFlag.TIMEOUT);
                return this;
            }

            public Builder withRetryCount(final byte retryCount) {
                this.retryCount = retryCount;
                attributeFlags.add(AttributeFlag.RETRY_CNT);
                return this;
            }

            public Builder withRnrRetryCount(final byte rnrRetryCount) {
                this.rnrRetryCount = rnrRetryCount;
                attributeFlags.add(AttributeFlag.RNR_RETRY);
                return this;
            }

            public Builder withAlternatePortNumber(final byte alternatePortNumber) {
                this.alternatePortNumber = alternatePortNumber;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateTimeout(final byte alternateTimeout) {
                this.alternateTimeout = alternateTimeout;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withRateLimit(final int rateLimit) {
                this.rateLimit = rateLimit;
                attributeFlags.add(AttributeFlag.RATE_LIMIT);
                return this;
            }

            public Builder withMaxSendWorkRequests(final int maxSendWorkRequests) {
                this.maxSendWorkRequests = maxSendWorkRequests;
                attributeFlags.add(AttributeFlag.CAP);
                return this;
            }

            public Builder withMaxReceiveWorkRequests(final int maxReceiveWorkRequests) {
                this.maxReceiveWorkRequests = maxReceiveWorkRequests;
                attributeFlags.add(AttributeFlag.CAP);
                return this;
            }

            public Builder withMaxSendScatterGatherElements(final int maxSendScatterGatherElements) {
                this.maxSendScatterGatherElements = maxSendScatterGatherElements;
                attributeFlags.add(AttributeFlag.CAP);
                return this;
            }

            public Builder withMaxReceiveScatterGatherElements(final int maxReceiveScatterGatherElements) {
                this.maxReceiveScatterGatherElements = maxReceiveScatterGatherElements;
                attributeFlags.add(AttributeFlag.CAP);
                return this;
            }

            public Builder withMaxInlineData(final int maxInlineData) {
                this.maxInlineData = maxInlineData;
                attributeFlags.add(AttributeFlag.CAP);
                return this;
            }

            public Builder withRemoteLocalId(final short remoteLocalId) {
                this.remoteLocalId = remoteLocalId;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withServiceLevel(final byte serviceLevel) {
                this.serviceLevel = serviceLevel;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withSourcePathBits(final byte sourcePathBits) {
                this.sourcePathBits = sourcePathBits;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withStaticRate(final byte staticRate) {
                this.staticRate = staticRate;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withIsGlobal(final boolean isGlobal) {
                this.isGlobal = isGlobal;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withRemotePortNumber(final byte remotePortNumber) {
                this.remotePortNumber = remotePortNumber;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withRemoteGlobalId(final long remoteGlobalId) {
                this.remoteGlobalId = remoteGlobalId;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withFlowLabel(final int flowLabel) {
                this.flowLabel = flowLabel;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withIndex(final byte index) {
                this.index = index;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withHopLimit(final byte hopLimit) {
                this.hopLimit = hopLimit;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withTrafficClass(final byte trafficClass) {
                this.trafficClass = trafficClass;
                attributeFlags.add(AttributeFlag.AV);
                return this;
            }

            public Builder withAlternateRemoteLocalId(final short alternateRemoteLocalId) {
                this.alternateRemoteLocalId = alternateRemoteLocalId;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateServiceLevel(final byte alternateServiceLevel) {
                this.alternateServiceLevel = alternateServiceLevel;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateSourcePathBits(final byte alternateSourcePathBits) {
                this.alternateSourcePathBits = alternateSourcePathBits;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateStaticRate(final byte alternateStaticRate) {
                this.alternateStaticRate = alternateStaticRate;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateIsGlobal(final boolean alternateIsGlobal) {
                this.alternateIsGlobal = alternateIsGlobal;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateRemotePortNumber(final byte alternateRemotePortNumber) {
                this.alternateRemotePortNumber = alternateRemotePortNumber;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateRemoteGlobalId(final long alternateRemoteGlobalId) {
                this.alternateRemoteGlobalId = alternateRemoteGlobalId;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateFlowLabel(final int alternateFlowLabel) {
                this.alternateFlowLabel = alternateFlowLabel;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateIndex(final byte alternateIndex) {
                this.alternateIndex = alternateIndex;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateHopLimit(final byte alternateHopLimit) {
                this.alternateHopLimit = alternateHopLimit;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Builder withAlternateTrafficClass(final byte alternateTrafficClass) {
                this.alternateTrafficClass = alternateTrafficClass;
                attributeFlags.add(AttributeFlag.ALT_PATH);
                return this;
            }

            public Attributes build() {
                var ret = new Attributes();

                if(state != null) ret.setState(state);
                if(currentState != null) ret.setCurrentState(currentState);
                if(pathMtu != null) ret.setPathMtu(pathMtu);
                if(pathMigrationState != null) ret.setPathMigrationState(pathMigrationState);
                if(accessFlags != null) ret.setAccessFlags(accessFlags);

                ret.setQkey(qkey);
                ret.setReceivePacketNumber(receivePacketNumber);
                ret.setSendPacketNumber(sendPacketNumber);
                ret.setRemoteQueuePairNumber(remoteQueuePairNumber);
                ret.setPartitionKeyIndex(partitionKeyIndex);
                ret.setAlternatePartitionKeyIndex(alternatePartitionKeyIndex);
                ret.setNotifyDrained(notifyDrained);
                ret.setMaxInitiatorAtomicReads(maxInitiatorAtomicReads);
                ret.setMaxDestinationAtomicReads(maxDestinationAtomicReads);
                ret.setMinRnrTimer(minRnrTimer);
                ret.setPortNumber(portNumber);
                ret.setTimeout(timeout);
                ret.setRetryCount(retryCount);
                ret.setRnrRetryCount(rnrRetryCount);
                ret.setAlternatePortNumber(alternatePortNumber);
                ret.setAlternateTimeout(alternateTimeout);
                ret.setRateLimit(rateLimit);

                ret.capabilities.setMaxSendWorkRequests(maxSendWorkRequests);
                ret.capabilities.setMaxReceiveWorkRequests(maxReceiveWorkRequests);
                ret.capabilities.setMaxSendScatterGatherElements(maxSendScatterGatherElements);
                ret.capabilities.setMaxReceiveScatterGatherElements(maxReceiveScatterGatherElements);
                ret.capabilities.setMaxInlineData(maxInlineData);

                ret.addressHandle.setRemoteLocalId(remoteLocalId);
                ret.addressHandle.setServiceLevel(serviceLevel);
                ret.addressHandle.setSourcePathBits(sourcePathBits);
                ret.addressHandle.setStaticRate(staticRate);
                ret.addressHandle.setIsGlobal(isGlobal);
                ret.addressHandle.setPortNumber(remotePortNumber);
                ret.addressHandle.globalRoute.setRemoteGlobalId(remoteGlobalId);
                ret.addressHandle.globalRoute.setFlowLabel(flowLabel);
                ret.addressHandle.globalRoute.setIndex(index);
                ret.addressHandle.globalRoute.setHopLimit(hopLimit);
                ret.addressHandle.globalRoute.setTrafficClass(trafficClass);

                ret.alternateAddressHandle.setRemoteLocalId(alternateRemoteLocalId);
                ret.alternateAddressHandle.setServiceLevel(alternateServiceLevel);
                ret.alternateAddressHandle.setSourcePathBits(alternateSourcePathBits);
                ret.alternateAddressHandle.setStaticRate(alternateStaticRate);
                ret.alternateAddressHandle.setIsGlobal(alternateIsGlobal);
                ret.alternateAddressHandle.setPortNumber(alternateRemotePortNumber);
                ret.alternateAddressHandle.globalRoute.setRemoteGlobalId(alternateRemoteGlobalId);
                ret.alternateAddressHandle.globalRoute.setFlowLabel(alternateFlowLabel);
                ret.alternateAddressHandle.globalRoute.setIndex(alternateIndex);
                ret.alternateAddressHandle.globalRoute.setHopLimit(alternateHopLimit);
                ret.alternateAddressHandle.globalRoute.setTrafficClass(alternateTrafficClass);

                return ret;
            }

            public AttributeFlag[] getAttributeFlags() {
                return attributeFlags.toArray(new AttributeFlag[0]);
            }
        }
    }
}
