package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import org.openucx.*;

import static org.openucx.OpenUcx.*;

public class RequestParameters extends NativeObject {

    static final RequestParameters EMPTY = new RequestParameters();

    // Prevent Garbage Collection
    private SendCallback sendCallback;
    private ReceiveCallback receiveCallback;

    public RequestParameters() {
        this(ResourceScope.newImplicitScope());
    }

    public RequestParameters(ResourceScope scope) {
        super(ucp_request_param_t.allocate(scope));
    }

    public RequestParameters setUserData(long data) {
        ucp_request_param_t.user_data$set(segment(), MemoryAddress.ofLong(data));
        addAttributeMask(Attribute.USER_DATA);
        return this;
    }

    public RequestParameters setSendCallback(SendCallback callback) {
        this.sendCallback = callback;
        ucp_request_param_t.cb.send$set(ucp_request_param_t.cb$slice(segment()), callback.upcallStub().address());
        addAttributeMask(Attribute.CALLBACK);
        return this;
    }

    public RequestParameters setReceiveCallback(ReceiveCallback callback) {
        this.receiveCallback = callback;
        ucp_request_param_t.cb.recv$set(ucp_request_param_t.cb$slice(segment()), callback.upcallStub().address());
        addAttributeMask(Attribute.CALLBACK);
        return this;
    }

    public RequestParameters setStreamCallback(StreamCallback callback) {
        ucp_request_param_t.cb.recv$set(ucp_request_param_t.cb$slice(segment()), callback.upcallStub().address());
        addAttributeMask(Attribute.CALLBACK);
        return this;
    }

    public RequestParameters setReplyBuffer(MemorySegment replyBuffer) {
        ucp_request_param_t.reply_buffer$set(segment(), replyBuffer.address());
        addAttributeMask(Attribute.REPLY_BUFFER);
        return this;
    }

    public RequestParameters setDataType(DataType dataType) {
        ucp_request_param_t.datatype$set(segment(), dataType.identifier());
        addAttributeMask(Attribute.DATATYPE);
        return this;
    }

    public RequestParameters setRequest(Request request) {
        ucp_request_param_t.request$set(segment(), request.address());
        addAttributeMask(Attribute.REQUEST);
        return this;
    }

    public RequestParameters setFlags(Flag... flags) {
        ucp_request_param_t.flags$set(segment(), BitMask.intOf(flags));
        addAttributeMask(Attribute.FLAGS);
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
        CALLBACK(UCP_OP_ATTR_FIELD_CALLBACK()),
        DATATYPE(UCP_OP_ATTR_FIELD_DATATYPE()),
        REQUEST(UCP_OP_ATTR_FIELD_REQUEST()),
        REPLY_BUFFER(UCP_OP_ATTR_FIELD_REPLY_BUFFER()),
        USER_DATA(UCP_OP_ATTR_FIELD_USER_DATA()),
        FLAGS(UCP_OP_ATTR_FIELD_FLAGS());


        private final int value;

        Attribute(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum Flag implements IntegerFlag {
        STREAM_WAIT(UCP_STREAM_RECV_FLAG_WAITALL()),
        ACTIVE_MESSAGE_REPLY(UCP_AM_SEND_FLAG_REPLY()),
        ACTIVE_MESSAGE_EAGER(UCP_AM_SEND_FLAG_EAGER()),
        ACTIVE_MESSAGE_RENDEVOUZ(UCP_AM_SEND_FLAG_RNDV());

        private final int value;

        Flag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
