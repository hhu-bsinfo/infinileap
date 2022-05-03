package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;
import org.openucx.ucp_am_recv_param_t;

import static org.openucx.OpenUcx.*;

public class ReceiveParameters extends NativeObject {

    public ReceiveParameters() {
        this(ResourceScope.newImplicitScope());
    }

    public ReceiveParameters(ResourceScope scope) {
        super(ucp_am_recv_param_t.allocate(scope));
    }

    public MemoryAddress getReplyEndpoint() {
        return BitMask.isSet(ucp_am_recv_param_t.recv_attr$get(segment()), Field.REPLY_EP) ?
               ucp_am_recv_param_t.reply_ep$get(segment()) : MemoryAddress.NULL;
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
