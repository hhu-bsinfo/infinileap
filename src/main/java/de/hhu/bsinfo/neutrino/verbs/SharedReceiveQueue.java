package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import java.nio.ByteBuffer;

public class SharedReceiveQueue extends Struct {

    private final NativeLong context = longField("context");
    private final NativeLong userContext = longField("srq_context");
    private final NativeLong protectionDomain = longField("pd");
    private final NativeInteger handle = integerField("handle");
    private final NativeLong mutex = longField("mutex");
    private final NativeLong cond = longField("cond");
    private final NativeInteger eventsCompleted = integerField("events_completed");

    public SharedReceiveQueue(long handle) {
        super("ibv_srq");
    }

    public static final class InitialAttributes extends Struct {

        private final NativeLong context = longField("srq_context");
        private final Attributes attributes = valueField("attr", Attributes::new);

        public InitialAttributes() {
            super("ibv_srq_init_attr");
        }

        public InitialAttributes(long handle) {
            super("ibv_srq_init_attr", handle);
        }
    }

    public static final class Attributes extends Struct {

        private final NativeInteger maxWorkRequest = integerField("max_wr");
        private final NativeInteger maxScatterGatherElements = integerField("max_sge");
        private final NativeInteger limit = integerField("srq_limit");

        public Attributes() {
            super("ibv_srq_attr");
        }

        public Attributes(long handle) {
            super("ibv_srq_attr", handle);
        }

        public Attributes(ByteBuffer buffer, int offset) {
            super("ibv_srq_attr", buffer, offset);
        }
    }
}
