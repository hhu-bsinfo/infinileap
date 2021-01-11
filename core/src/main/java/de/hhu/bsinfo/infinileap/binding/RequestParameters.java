package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.C;
import org.openucx.ucx_h;
import org.openucx.ucx_h.*;

public class RequestParameters extends NativeObject {

    static final RequestParameters EMPTY = new RequestParameters();

    public RequestParameters() {
        super(ucp_request_param_t.allocate());
    }

    public RequestParameters setUserData(long data) {
        ucp_request_param_t.user_data$set(segment(), MemoryAddress.ofLong(data));
        addAttributeMask(Attribute.USER_DATA);
        return this;
    }

    public RequestParameters setSendCallback(SendCallback callback) {
        ucp_request_param_t.cb.send$set(ucp_request_param_t.cb$slice(segment()), callback.upcallStub().address());
        addAttributeMask(Attribute.CALLBACK);
        return this;
    }

    public RequestParameters setReceiveCallback(ReceiveCallback callback) {
        ucp_request_param_t.cb.recv$set(ucp_request_param_t.cb$slice(segment()), callback.upcallStub().address());
        addAttributeMask(Attribute.CALLBACK);
        return this;
    }

    protected int getAttributeMask() {
        return ucp_request_param_t.op_attr_mask$get(segment());
    }

    protected void setAttributeMsk(Attribute... fields) {
        ucp_request_param_t.op_attr_mask$set(segment(), BitMask.intOf(fields));
    }

    protected void addAttributeMask(Attribute... fields) {
        ucp_request_param_t.op_attr_mask$set(segment(), BitMask.intOf(fields) | getAttributeMask());
    }

    public enum Attribute implements IntegerFlag {
        CALLBACK(ucx_h.UCP_OP_ATTR_FIELD_CALLBACK()),
        DATATYPE(ucx_h.UCP_OP_ATTR_FIELD_DATATYPE()),
        REQUEST(ucx_h.UCP_OP_ATTR_FIELD_REQUEST()),
        REPLY_BUFFER(ucx_h.UCP_OP_ATTR_FIELD_REPLY_BUFFER()),
        USER_DATA(ucx_h.UCP_OP_ATTR_FIELD_USER_DATA()),
        FLAGS(ucx_h.UCP_OP_ATTR_FIELD_FLAGS());


        private final int value;

        Attribute(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
