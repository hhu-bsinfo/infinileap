package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import org.openucx.ucp_am_recv_param_t;

import static org.openucx.OpenUcx.*;

public class ReceiveParameters extends NativeObject {

    public ReceiveParameters() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public ReceiveParameters(SegmentAllocator allocator) {
        super(ucp_am_recv_param_t.allocate(allocator));
    }

    public MemorySegment getReplyEndpoint() {
        return BitMask.isSet(ucp_am_recv_param_t.recv_attr$get(segment()), Field.REPLY_EP) ?
               ucp_am_recv_param_t.reply_ep$get(segment()) : MemorySegment.NULL;
    }

    public enum Field implements LongFlag {
        REPLY_EP(UCP_AM_RECV_ATTR_FIELD_REPLY_EP()),
        DATA(UCP_AM_RECV_ATTR_FLAG_DATA()),
        RENDEZVOUS(UCP_AM_RECV_ATTR_FLAG_RNDV());

        private final long value;

        Field(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }
}
