package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import java.nio.ByteBuffer;

@LinkNative(value = "ibv_srq")
public class SharedReceiveQueue extends Struct {

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

    @LinkNative(value = "ibv_srq_init_attr")
    public static final class InitialAttributes extends Struct {

        private final NativeLong context = longField("srq_context");
        public final Attributes attributes = valueField("attr", Attributes::new);

        public InitialAttributes() {
        }

        public InitialAttributes(long handle) {
            super(handle);
        }

        public long getContext() {
            return context.get();
        }
    }

    @LinkNative(value = "ibv_srq_attr")
    public static final class Attributes extends Struct {

        private final NativeInteger maxWorkRequest = integerField("max_wr");
        private final NativeInteger maxScatterGatherElements = integerField("max_sge");
        private final NativeInteger limit = integerField("srq_limit");

        public Attributes() {
        }

        public Attributes(long handle) {
            super(handle);
        }

        public Attributes(LocalBuffer buffer, int offset) {
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
    }
}
