package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.*;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

@LinkNative("ibv_wq")
public class WorkQueue extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkQueue.class);

    private final Context context = referenceField("context", Context::new);
    private final NativeLong userContext = longField("wq_context");
    private final ProtectionDomain protectionDomain = referenceField("pd", ProtectionDomain::new);
    private final CompletionQueue completionQueue = referenceField("cq", CompletionQueue::new);
    private final NativeInteger workQueueNumber = integerField("wq_num");
    private final NativeInteger state = integerField("state");
    private final NativeEnum<Type> type = enumField("wq_type", Type.CONVERTER);
    private final NativeInteger eventsCompleted = integerField("events_completed");
    private final NativeIntegerBitMask<CompatibilityFlag> compatibilityMask = intBitField("comp_mask");

    WorkQueue(final long handle) {
        super(handle);
    }

    WorkQueue(final LocalBuffer buffer, final long offset) {
        super(buffer, offset);
    }

    private boolean postReceive(final long receiveWorkRequestsHandle) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.postReceiveWorkRequestWorkQueue(getHandle(), receiveWorkRequestsHandle, result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Posting receive work requests to work queue failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public boolean modify(Attributes attributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.modifyWorkQueue(getHandle(), attributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Modifying work queue failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();

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
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.destroyWorkQueue(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Destroying work queue failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();
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
    
    public enum AttributeFlag implements Flag {
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

    public enum CompatibilityFlag implements Flag {
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

    public enum CreationFlag implements Flag {
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
        private final NativeIntegerBitMask<CompatibilityFlag> compatibilityMask = intBitField("comp_mask");
        private final NativeIntegerBitMask<CreationFlag> flags = intBitField("create_flags");

        public InitialAttributes() {}

        public InitialAttributes(final Consumer<InitialAttributes> configurator) {
            configurator.accept(this);
        }

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

        public long getProtectionDomain() {
            return protectionDomain.get();
        }

        public long getCompletionQueue() {
            return completionQueue.get();
        }

        public int getCompatibilityMask() {
            return compatibilityMask.get();
        }

        public int getFlags() {
            return flags.get();
        }

        public void setUserContext(final long value) {
            userContext.set(value);
        }

        public void setType(final Type value) {
            type.set(value);
        }

        public void setMaxWorkRequests(final int value) {
            maxWorkRequests.set(value);
        }

        public void setMaxScatterGatherElements(final int value) {
            maxScatterGatherElements.set(value);
        }

        public void setProtectionDomain(final ProtectionDomain protectionDomain) {
            this.protectionDomain.set(protectionDomain.getHandle());
        }

        public void setCompletionQueue(final CompletionQueue completionQueue) {
            this.completionQueue.set(completionQueue.getHandle());
        }

        public void setCompatibilityMask(final CompatibilityFlag... flags) {
            compatibilityMask.set(flags);
        }

        public void setFlags(final CreationFlag... flags) {
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
    }

    @LinkNative("ibv_wq_attr")
    public static final class Attributes extends Struct {

        private final NativeIntegerBitMask<AttributeFlag> attributesMask = intBitField("attr_mask");
        private final NativeEnum<State> state = enumField("wq_state", State.CONVERTER);
        private final NativeEnum<State> currentState = enumField("curr_wq_state", State.CONVERTER);
        private final NativeIntegerBitMask<CreationFlag> flags = intBitField("flags");
        private final NativeIntegerBitMask<CreationFlag> flagsMask = intBitField("flags_mask");

        public Attributes() {}

        public Attributes(final Consumer<Attributes> configurator) {
            configurator.accept(this);
        }

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

        public void setAttributesMask(final AttributeFlag... flags) {
            attributesMask.set(flags);
        }

        public void setState(final State value) {
            state.set(value);
        }

        public void setCurrentState(final State value) {
            currentState.set(value);
        }

        public void setFlags(final CreationFlag... flags) {
            this.flags.set(flags);
        }

        public void setFlagsMask(final CreationFlag... value) {
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
    }
}
