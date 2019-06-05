package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeBitMask;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import java.util.Arrays;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative(value = "ibv_srq")
public class SharedReceiveQueue extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedReceiveQueue.class);

    private final Context context = referenceField("context", Context::new);
    private final NativeLong userContext = longField("srq_context");
    private final ProtectionDomain protectionDomain = referenceField("pd", ProtectionDomain::new);
    private final NativeInteger eventsCompleted = integerField("events_completed");

    SharedReceiveQueue(long handle) {
        super(handle);
    }

    public Context getContext() {
        return context;
    }

    public NativeLong getUserContext() {
        return userContext;
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    public NativeInteger getEventsCompleted() {
        return eventsCompleted;
    }

    public boolean modify(Attributes attributes, AttributeFlag... flags) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.modifySharedReceiveQueue(getHandle(), attributes.getHandle(), BitMask.of(flags), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Modifying shared receive queue failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();

        return !isError;
    }

    public Attributes queryAttributes() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        var attributes = new Attributes();

        Verbs.querySharedReceiveQueue(getHandle(), attributes.getHandle(), result.getHandle());
        boolean isError = result.isError();
        if (isError) {
            LOGGER.error("Querying shared receive queue failed with error [{}]", result.getStatus());
            attributes = null;
        }

        result.releaseInstance();

        return attributes;
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.destroySharedReceiveQueue(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Destroying shared receive failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();
    }

    public enum AttributeFlag implements Flag {
        MAX_WR(1), LIMIT(1 << 1);

        private final int value;

        AttributeFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum ExtendedAttributeFlag implements Flag {
        TYPE(1), PD(1 << 1), XRCD(1 << 2),
        CQ(1 << 3), TM(1 << 4), RESERVED(1 << 5);

        private final int value;

        ExtendedAttributeFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum Type {
        BASIC(1), XRC(2), TM(3);

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
                if (integer < BASIC.value || integer > TM.value) {
                    throw new IllegalArgumentException(String.format("Unknown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    @LinkNative(value = "ibv_srq_attr")
    public static final class Attributes extends Struct {

        private final NativeInteger maxWorkRequest = integerField("max_wr");
        private final NativeInteger maxScatterGatherElements = integerField("max_sge");
        private final NativeInteger limit = integerField("srq_limit");

        Attributes() {}

        Attributes(LocalBuffer buffer, long offset) {
            super(buffer, offset);
        }

        public int getMaxWorkRequest() {
            return maxWorkRequest.get();
        }

        public void setMaxWorkRequest(int maxWorkRequest) {
            this.maxWorkRequest.set(maxWorkRequest);
        }

        public int getMaxScatterGatherElements() {
            return maxScatterGatherElements.get();
        }

        public void setMaxScatterGatherElements(int maxScatterGatherElements) {
            this.maxScatterGatherElements.set(maxScatterGatherElements);
        }

        public int getLimit() {
            return limit.get();
        }

        public void setLimit(int limit) {
            this.limit.set(limit);
        }

        @Override
        public String toString() {
            return "Attributes {" +
                "\n\tmaxWorkRequest=" + maxWorkRequest +
                ",\n\tmaxScatterGatherElements=" + maxScatterGatherElements +
                ",\n\tlimit=" + limit +
                "\n}";
        }
    }

    @LinkNative(value = "ibv_srq_init_attr")
    public static final class InitialAttributes extends Struct {

        private final NativeLong userContext = longField("srq_context");

        public final Attributes attributes = valueField("attr", Attributes::new);

        public InitialAttributes() {}

        public InitialAttributes(final Consumer<InitialAttributes> configurator) {
            configurator.accept(this);
        }

        public long getUserContext() {
            return userContext.get();
        }

        public void setUserContext(long value) {
            this.userContext.set(value);
        }

        @Override
        public String toString() {
            return "InitialAttributes {" +
                "\n\tuserContext=" + userContext +
                ",\n\tattributes=" + attributes +
                "\n}";
        }
    }

    @LinkNative("ibv_srq_init_attr_ex")
    public static final class ExtendedInitialAttributes extends Struct {

        private final NativeLong userContext = longField("srq_context");
        private final NativeBitMask<ExtendedAttributeFlag> attributesMask = bitField("comp_mask");
        private final NativeEnum<Type> type = enumField("srq_type", Type.CONVERTER);
        private final NativeLong protectionDomain = longField("pd");
        private final NativeLong extendedConnectionDomain = longField("xrcd");
        private final NativeLong completionQueue = longField("cq");

        public final Attributes attributes = valueField("attr", Attributes::new);
        public final TagMatchingCapabilities tagMatchingCapabilities = valueField("tm_cap", TagMatchingCapabilities::new);

        public ExtendedInitialAttributes() {}

        public ExtendedInitialAttributes(final Consumer<ExtendedInitialAttributes> configurator) {
            configurator.accept(this);
        }

        public long getUserContext() {
            return userContext.get();
        }

        public int getCompatibilityMask() {
            return attributesMask.get();
        }

        public Type getType() {
            return type.get();
        }

        public long getProtectionDomain() {
            return protectionDomain.get();
        }

        public long getExtendedConnectionDomain() {
            return extendedConnectionDomain.get();
        }

        public long getCompletionQueue() {
            return completionQueue.get();
        }

        public void setUserContext(final int value) {
            userContext.set(value);
        }

        public void setAttributesMask(final ExtendedAttributeFlag... flags) {
            attributesMask.set(flags);
        }

        public void setType(final Type value) {
            type.set(value);
        }

        public void setProtectionDomain(final ProtectionDomain protectionDomain) {
            this.protectionDomain.set(protectionDomain.getHandle());
        }

        public void setExtendedConnectionDomain(final ExtendedConnectionDomain extendedConnectionDomain) {
            this.extendedConnectionDomain.set(extendedConnectionDomain.getHandle());
        }

        public void setCompletionQueue(final CompletionQueue completionQueue) {
            this.completionQueue.set(completionQueue.getHandle());
        }

        @Override
        public String toString() {
            return "ExtendedInitialAttributes {" +
                "\n\tuserContext=" + userContext +
                ",\n\tattributesMask=" + attributesMask +
                ",\n\ttype=" + type +
                ",\n\tprotectionDomain=" + protectionDomain +
                ",\n\textendedConnectionDomain=" + extendedConnectionDomain +
                ",\n\tcompletionQueue=" + completionQueue +
                ",\n\tattributes=" + attributes +
                ",\n\ttagMatchingCapabilities=" + tagMatchingCapabilities +
                "\n}";
        }
    }

    @LinkNative("ibv_tm_cap")
    public static final class TagMatchingCapabilities extends Struct {

        private final NativeInteger maxTags = integerField("max_num_tags");
        private final NativeInteger maxOperations = integerField("max_ops");

        TagMatchingCapabilities(LocalBuffer buffer, long offset) {
            super(buffer, offset);
        }

        public int getMaxTags() {
            return maxTags.get();
        }

        public int getMaxOperations() {
            return maxOperations.get();
        }

        public void setMaxTags(final int value) {
            maxTags.set(value);
        }

        public void setMaxOperations(final int value) {
            maxOperations.set(value);
        }

        @Override
        public String toString() {
            return "TagMatchingCapabilities {" +
                "\n\tmaxTags=" + maxTags +
                ",\n\tmaxOperations=" + maxOperations +
                "\n}";
        }
    }
}
