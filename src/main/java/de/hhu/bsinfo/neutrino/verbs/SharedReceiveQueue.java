package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;

public class SharedReceiveQueue extends Struct {

    private static final StructInformation INFO = StructUtil.getInfo("ibv_srq");
    public static final int SIZE = INFO.structSize.get();

    private final NativeLong context = new NativeLong(getByteBuffer(), INFO.getOffset("context"));
    private final NativeLong userContext = new NativeLong(getByteBuffer(), INFO.getOffset("srq_context"));
    private final NativeLong protectionDomain = new NativeLong(getByteBuffer(), INFO.getOffset("pd"));
    private final NativeInteger handle = new NativeInteger(getByteBuffer(), INFO.getOffset("handle"));
    private final NativeLong mutex = new NativeLong(getByteBuffer(), INFO.getOffset("mutex"));
    private final NativeLong cond = new NativeLong(getByteBuffer(), INFO.getOffset("cond"));
    private final NativeInteger eventsCompleted = new NativeInteger(getByteBuffer(), INFO.getOffset("events_completed"));

    public SharedReceiveQueue(long handle) {
        super(handle, SIZE);
    }

    public static final class Attributes extends Struct {

        private static final StructInformation INIT_INFO = StructUtil.getInfo("ibv_srq_init_attr");
        private static final int INIT_SIZE = INIT_INFO.structSize.get();

        private static final StructInformation ATTRIBUTES_INFO = StructUtil.getInfo("ibv_srq_attr");
        private static final int ATTR_OFFSET = INIT_INFO.getOffset("attr");


        public static final int ATTRIBUTES_SIZE = INIT_INFO.structSize.get();

        private final NativeLong context = new NativeLong(getByteBuffer(), ATTRIBUTES_INFO.getOffset("srq_context"));

        private final NativeInteger maxWorkRequest = new NativeInteger(getByteBuffer(), ATTR_OFFSET + ATTRIBUTES_INFO.getOffset("max_wr"));
        private final NativeInteger maxScatterGatherElements = new NativeInteger(getByteBuffer(), ATTR_OFFSET + ATTRIBUTES_INFO.getOffset("max_sge"));
        private final NativeInteger limit = new NativeInteger(getByteBuffer(), ATTR_OFFSET + ATTRIBUTES_INFO.getOffset("srq_limit"));

        public Attributes() {
            super(INIT_SIZE);
        }

        public Attributes(long handle) {
            super(handle, INIT_SIZE);
        }
    }
}
