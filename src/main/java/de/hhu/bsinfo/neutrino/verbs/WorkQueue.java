package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.*;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import de.hhu.bsinfo.neutrino.util.flag.LongFlag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.NativeObjectRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@LinkNative("ibv_wq")
public class WorkQueue extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkQueue.class);

    private final Context context = referenceField("context");
    private final NativeLong userContext = longField("wq_context");
    private final ProtectionDomain protectionDomain = referenceField("pd");
    private final CompletionQueue completionQueue = referenceField("cq");
    private final NativeInteger workQueueNumber = integerField("wq_num");
    private final NativeInteger state = integerField("state");
    private final NativeEnum<Type> type = enumField("wq_type", Type.CONVERTER);
    private final NativeInteger eventsCompleted = integerField("events_completed");
    private final NativeIntegerBitMask<CompatibilityFlag> compatibilityMask = integerBitField("comp_mask");

    WorkQueue(final long handle) {
        super(handle);
    }

    WorkQueue(final LocalBuffer buffer, final long offset) {
        super(buffer, offset);
    }

    private boolean postReceive(final long receiveWorkRequestsHandle) {
        var result = Result.localInstance();

        Verbs.postReceiveWorkRequestWorkQueue(getHandle(), receiveWorkRequestsHandle, result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Posting receive work requests to work queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        return !isError;
    }

    public boolean modify(Attributes attributes) {
        var result = Result.localInstance();

        Verbs.modifyWorkQueue(getHandle(), attributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Modifying work queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        }

        return !isError;
    }

    public boolean postReceive(final ReceiveWorkRequest receiveWorkRequest) {
        return postReceive(receiveWorkRequest.getHandle());
    }

    public boolean postReceive(final NativeLinkedList<ReceiveWorkRequest> receiveWorkRequests) {
        return postReceive(receiveWorkRequests.getHandle());
    }
    @Override
    public void close() {
        var result = Result.localInstance();

        Verbs.destroyWorkQueue(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Destroying work queue failed with error [{}]: {}", result.getStatus(), result.getStatusMessage());
        } else {
            NativeObjectRegistry.deregisterObject(this);
        }
    }

    public Context getContext() {
        return context;
    }

    public long getUserContext() {
        return userContext.get();
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    public CompletionQueue getCompletionQueue() {
        return completionQueue;
    }

    public int getWorkQueueNumber() {
        return workQueueNumber.get();
    }

    public int getState() {
        return state.get();
    }

    public Type getType() {
        return type.get();
    }

    public int getEventsCompleted() {
        return eventsCompleted.get();
    }

    public int getCompatibilityMask() {
        return compatibilityMask.get();
    }

    @Override
    public String toString() {
        return "WorkQueue {" +
                "\n\tuserContext=" + userContext +
                ",\n\tcompletionQueue=" + completionQueue +
                ",\n\tworkQueueNumber=" + workQueueNumber +
                ",\n\tstate=" + state +
                ",\n\ttype=" + type +
                ",\n\teventsCompleted=" + eventsCompleted +
                ",\n\tcompatibilityMask=" + compatibilityMask +
                "\n}";
    }

    public enum Type {
        RQ(0);

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
                if (integer < RQ.value || integer > RQ.value) {
                    throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum State {
        RESET(0), READY(1), ERROR(2), UNKNOWN(3);

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
                    throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }
    
    public enum AttributeFlag implements IntegerFlag {
        STATE(1), CURRENT_STATE(1 << 1), FLAGS(1 << 2), RESERVED(1 << 3);

        private final int value;

        AttributeFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum CompatibilityFlag implements IntegerFlag {
        FLAGS(1), RESERVED(1 << 1);

        private final int value;

        CompatibilityFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum CreationFlag implements IntegerFlag {
        CVLAN_STRIPPING(1), SCATTER_FCS(1 << 1), DELAY_DROP(1 << 2),
        PCI_WRITE_END_PADDING(1 << 3), RESERVED(1 << 4);

        private final int value;

        CreationFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    @LinkNative("ibv_wq_init_attr")
    public static final class InitialAttributes extends Struct {

        private final NativeLong userContext = longField("wq_context");
        private final NativeEnum<Type> type = enumField("wq_type", Type.CONVERTER);
        private final NativeInteger maxWorkRequests = integerField("max_wr");
        private final NativeInteger maxScatterGatherElements = integerField("max_sge");
        private final NativeLong protectionDomain = longField("pd");
        private final NativeLong completionQueue = longField("cq");
        private final NativeIntegerBitMask<CompatibilityFlag> compatibilityMask = integerBitField("comp_mask");
        private final NativeIntegerBitMask<CreationFlag> flags = integerBitField("create_flags");

        InitialAttributes() {}

        public long getUserContext() {
            return userContext.get();
        }

        public Type getType() {
            return type.get();
        }

        public int getMaxWorkRequests() {
            return maxWorkRequests.get();
        }

        public int getMaxScatterGatherElements() {
            return maxScatterGatherElements.get();
        }

        public ProtectionDomain getProtectionDomain() {
            return NativeObjectRegistry.getObject(protectionDomain.get());
        }

        public CompletionQueue getCompletionQueue() {
            return NativeObjectRegistry.getObject(completionQueue.get());
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public int getFlags() {
            return flags.get();
        }

        void setUserContext(final long value) {
            userContext.set(value);
        }

        void setType(final Type value) {
            type.set(value);
        }

        void setMaxWorkRequests(final int value) {
            maxWorkRequests.set(value);
        }

        void setMaxScatterGatherElements(final int value) {
            maxScatterGatherElements.set(value);
        }

        void setProtectionDomain(final ProtectionDomain protectionDomain) {
            this.protectionDomain.set(protectionDomain.getHandle());
        }

        void setCompletionQueue(final CompletionQueue completionQueue) {
            this.completionQueue.set(completionQueue.getHandle());
        }

        void setCompatibilityMask(final CompatibilityFlag... flags) {
            compatibilityMask.set(flags);
        }

        void setFlags(final CreationFlag... flags) {
            this.flags.set(flags);
        }

        @Override
        public String toString() {
            return "InitialAttributes {" +
                    "\n\tuserContext=" + userContext +
                    ",\n\ttype=" + type +
                    ",\n\tmaxWorkRequests=" + maxWorkRequests +
                    ",\n\tmaxScatterGatherElements=" + maxScatterGatherElements +
                    ",\n\tprotectionDomain=" + protectionDomain +
                    ",\n\tcompletionQueue=" + completionQueue +
                    ",\n\tcompatibilityMask=" + compatibilityMask +
                    ",\n\tflags=" + flags +
                    "\n}";
        }

        public static final class Builder {

            private long userContext;
            private Type type;
            private int maxWorkRequests;
            private int maxScatterGatherElements;
            private ProtectionDomain protectionDomain;
            private CompletionQueue completionQueue;
            private CompatibilityFlag[] compatibilityMask;
            private CreationFlag[] flags;

            public Builder(final int maxWorkRequests, final int maxScatterGatherElements, final Type type, final ProtectionDomain protectionDomain, final CompletionQueue completionQueue) {
                this.maxWorkRequests = maxWorkRequests;
                this.maxScatterGatherElements = maxScatterGatherElements;
                this.type = type;
                this.protectionDomain = protectionDomain;
                this.completionQueue = completionQueue;
            }

            public Builder withUserContext(final long userContext) {
                this.userContext = userContext;
                return this;
            }

            public Builder withCompatibilityMask(final CompatibilityFlag... flags) {
                compatibilityMask = flags;
                return this;
            }

            public Builder withFlags(final CreationFlag... flags) {
                this.flags = flags;
                return this;
            }

            public InitialAttributes build() {
                var ret = new InitialAttributes();

                ret.setUserContext(userContext);
                ret.setMaxWorkRequests(maxWorkRequests);
                ret.setMaxScatterGatherElements(maxScatterGatherElements);

                if(type != null) ret.setType(type);
                if(protectionDomain != null) ret.setProtectionDomain(protectionDomain);
                if(completionQueue != null) ret.setCompletionQueue(completionQueue);
                if(compatibilityMask != null) ret.setCompatibilityMask(compatibilityMask);
                if(flags != null) ret.setFlags(flags);

                return ret;
            }
        }
    }

    @LinkNative("ibv_wq_attr")
    public static final class Attributes extends Struct {

        private final NativeIntegerBitMask<AttributeFlag> attributesMask = integerBitField("attr_mask");
        private final NativeEnum<State> state = enumField("wq_state", State.CONVERTER);
        private final NativeEnum<State> currentState = enumField("curr_wq_state", State.CONVERTER);
        private final NativeIntegerBitMask<CreationFlag> flags = integerBitField("flags");
        private final NativeIntegerBitMask<CreationFlag> flagsMask = integerBitField("flags_mask");

        Attributes() {}

        public int getAttributesMask() {
            return attributesMask.get();
        }

        public State getState() {
            return state.get();
        }

        public State getCurrentState() {
            return currentState.get();
        }

        public int getFlags() {
            return flags.get();
        }

        public int getFlagsMask() {
            return flagsMask.get();
        }

        void setAttributesMask(final AttributeFlag... flags) {
            attributesMask.set(flags);
        }

        void setState(final State value) {
            state.set(value);
        }

        void setCurrentState(final State value) {
            currentState.set(value);
        }

        void setFlags(final CreationFlag... flags) {
            this.flags.set(flags);
        }

        void setFlagsMask(final CreationFlag... value) {
            flagsMask.set(value);
        }

        @Override
        public String toString() {
            return "Attributes {" +
                    "\n\tattributesMask=" + attributesMask +
                    ",\n\tstate=" + state +
                    ",\n\tcurrentState=" + currentState +
                    ",\n\tflags=" + flags +
                    ",\n\tflagsMask=" + flagsMask +
                    "\n}";
        }

        public static final class Builder {

            private final Set<AttributeFlag> attributeFlags = new HashSet<>();
            private State state;
            private State currentState;
            private CreationFlag[] flags;
            private CreationFlag[] flagsMask;

            public Builder() {}

            public Builder withState(final State state) {
                this.state = state;
                attributeFlags.add(AttributeFlag.STATE);
                return this;
            }

            public Builder withCurrentState(final State currentState) {
                this.currentState = currentState;
                attributeFlags.add(AttributeFlag.CURRENT_STATE);
                return this;
            }

            public Builder withFlags(final CreationFlag... flags) {
                this.flags = flags;
                attributeFlags.add(AttributeFlag.FLAGS);
                return this;
            }

            public Builder withFlagsMask(final CreationFlag... flags) {
                flagsMask = flags;
                attributeFlags.add(AttributeFlag.FLAGS);
                return this;
            }

            public Attributes build() {
                var ret = new Attributes();

                ret.setAttributesMask(attributeFlags.toArray(new AttributeFlag[0]));

                if(state != null) ret.setState(state);
                if(currentState != null) ret.setCurrentState(currentState);
                if(flags != null) ret.setFlags(flags);
                if(flagsMask != null) ret.setFlagsMask(flagsMask);

                return ret;
            }
        }
    }
}
