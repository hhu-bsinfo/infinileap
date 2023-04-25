package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.flag.IntegerFlag;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import org.openucx.*;

import static org.openucx.OpenUcx.*;

public class RequestParameters extends NativeObject {

    static final RequestParameters EMPTY = new RequestParameters();

    private MemorySegment sendUpcall;
    private MemorySegment receiveUpcall;
    private MemorySegment streamUpcall;

    public RequestParameters() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public RequestParameters(SegmentAllocator allocator) {
        super(ucp_request_param_t.allocate(allocator));
    }

    public RequestParameters setUserData(long data) {
        ucp_request_param_t.user_data$set(segment(), MemorySegment.ofAddress(data));
        addAttributeMask(Attribute.USER_DATA);
        return this;
    }

    public RequestParameters setSendCallback(SendCallback callback) {
        sendUpcall = callback.upcallSegment();
        ucp_request_param_t.cb.send$set(ucp_request_param_t.cb$slice(segment()), sendUpcall);
        addAttributeMask(Attribute.CALLBACK);
        return this;
    }

    public RequestParameters setReceiveCallback(ReceiveCallback callback) {
        receiveUpcall = callback.upcallSegment();
        ucp_request_param_t.cb.recv$set(ucp_request_param_t.cb$slice(segment()), receiveUpcall);
        addAttributeMask(Attribute.CALLBACK);
        return this;
    }

    public RequestParameters setStreamCallback(StreamCallback callback) {
        streamUpcall = callback.upcallSegment();
        ucp_request_param_t.cb.recv$set(ucp_request_param_t.cb$slice(segment()), streamUpcall);
        addAttributeMask(Attribute.CALLBACK);
        return this;
    }

    public RequestParameters setReplyBuffer(MemorySegment replyBuffer) {
        ucp_request_param_t.reply_buffer$set(segment(), replyBuffer);
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

    public RequestParameters disableImmediateCompletion() {
        addAttributeMask(Attribute.NO_IMMEDIATE_COMPLETION);
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
        FLAGS(UCP_OP_ATTR_FIELD_FLAGS()),
        MEMORY_TYPE(UCP_OP_ATTR_FIELD_MEMORY_TYPE()),
        RECEIVE_INFO(UCP_OP_ATTR_FIELD_RECV_INFO()),
        NO_IMMEDIATE_COMPLETION(UCP_OP_ATTR_FLAG_NO_IMM_CMPL()),
        FAST_COMPLETION(UCP_OP_ATTR_FLAG_FAST_CMPL()),
        FORCE_IMMEDIATE_COMPLETION(UCP_OP_ATTR_FLAG_FORCE_IMM_CMPL());


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
        ACTIVE_MESSAGE_RENDEVOUZ(UCP_AM_SEND_FLAG_RNDV()),
        CLOSE_FORCE(UCP_EP_CLOSE_FLAG_FORCE());

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
