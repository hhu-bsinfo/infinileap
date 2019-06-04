package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;
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

    public boolean modify(Attributes attributes, AttributesFlag... flags) {
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

    public enum AttributesFlag implements Flag {
        MAX_WR(1), LIMIT(1 << 1);

        private final int value;

        AttributesFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    @LinkNative(value = "ibv_srq_init_attr")
    public static final class InitialAttributes extends Struct {

        private final NativeLong userContext = longField("srq_context");

        public final Attributes attributes = valueField("attr", Attributes::new);

        public InitialAttributes() {}

        public InitialAttributes(Consumer<InitialAttributes> configurator) {
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

    @LinkNative(value = "ibv_srq_attr")
    public static final class Attributes extends Struct {

        private final NativeInteger maxWorkRequest = integerField("max_wr");
        private final NativeInteger maxScatterGatherElements = integerField("max_sge");
        private final NativeInteger limit = integerField("srq_limit");

        public Attributes() {}

        public Attributes(Consumer<Attributes> configurator) {
            configurator.accept(this);
        }

        Attributes(LocalBuffer buffer, int offset) {
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
}
