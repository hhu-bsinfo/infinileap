package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.network.NativeInetSocketAddress;
import de.hhu.bsinfo.infinileap.common.util.flag.IntegerFlag;
import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import org.openucx.*;

import java.net.InetSocketAddress;

import static org.openucx.OpenUcx.*;

public class EndpointParameters extends NativeObject {

    private NativeInetSocketAddress remoteAddress;
    private MemorySegment errorUpcall;

    public EndpointParameters() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public EndpointParameters(SegmentAllocator allocator) {
        super(ucp_ep_params_t.allocate(allocator));
    }

    public EndpointParameters setRemoteAddress(WorkerAddress address) {
        ucp_ep_params_t.address$set(segment(), address.segment());
        addFieldMask(Field.REMOTE_ADDRESS);
        return this;
    }

    public EndpointParameters setRemoteAddress(InetSocketAddress socketAddress) {
        this.remoteAddress = NativeInetSocketAddress.convert(socketAddress);
        var sockaddr = ucp_ep_params_t.sockaddr$slice(segment());
        ucs_sock_addr_t.addr$set(sockaddr, remoteAddress.segment());
        ucs_sock_addr_t.addrlen$set(sockaddr, remoteAddress.getLength());

        addFieldMask(Field.SOCK_ADDR);
        addFlags(Flag.CLIENT_SERVER);
        return this;
    }

    public EndpointParameters setConnectionRequest(ConnectionRequest request) {
        ucp_ep_params_t.conn_request$set(segment(), request.base());
        addFieldMask(Field.CONN_REQUEST);
        return this;
    }

    public EndpointParameters setErrorHandler(ErrorHandler handler) {
        errorUpcall = handler.upcallSegment();
        ucp_err_handler_t.cb$set(ucp_ep_params_t.err_handler$slice(segment()), errorUpcall);
        ucp_ep_params_t.err_mode$set(segment(), ErrorHandlingMode.PEER.value());
        addFieldMask(Field.ERR_HANDLER, Field.ERR_HANDLING_MODE);
        return this;
    }

    public EndpointParameters enableClientIdentifier() {
        addFlags(Flag.SEND_CLIENT_ID);
        return this;
    }

    private long getFieldMask() {
        return ucp_ep_params_t.field_mask$get(segment());
    }

    private void setFieldMask(Field... fields) {
        ucp_ep_params_t.field_mask$set(segment(), BitMask.longOf(fields));
    }

    private void addFieldMask(Field... fields) {
        ucp_ep_params_t.field_mask$set(segment(), BitMask.longOf(fields) | getFieldMask());
    }

    private int getFlags() {
        return ucp_ep_params_t.flags$get(segment());
    }

    private void addFlags(Flag... flags) {
        ucp_ep_params_t.flags$set(segment(), BitMask.intOf(flags) | getFlags());
        addFieldMask(Field.FLAGS);
    }

    public enum Field implements LongFlag {
        REMOTE_ADDRESS(UCP_EP_PARAM_FIELD_REMOTE_ADDRESS()),
        CONN_REQUEST(UCP_EP_PARAM_FIELD_CONN_REQUEST()),
        ERR_HANDLER(UCP_EP_PARAM_FIELD_ERR_HANDLER()),
        ERR_HANDLING_MODE(UCP_EP_PARAM_FIELD_ERR_HANDLING_MODE()),
        SOCK_ADDR(UCP_EP_PARAM_FIELD_SOCK_ADDR()),
        USER_DATA(UCP_EP_PARAM_FIELD_USER_DATA()),
        FLAGS(UCP_EP_PARAM_FIELD_FLAGS());

        private final long value;

        Field(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    public enum Flag implements IntegerFlag {
        CLIENT_SERVER(UCP_EP_PARAMS_FLAGS_CLIENT_SERVER()),
        NO_LOOPBACK(UCP_EP_PARAMS_FLAGS_NO_LOOPBACK()),
        SEND_CLIENT_ID(UCP_EP_PARAMS_FLAGS_SEND_CLIENT_ID());

        private final int value;

        Flag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    private enum ErrorHandlingMode {
        NONE(UCP_ERR_HANDLING_MODE_NONE()),
        PEER(UCP_ERR_HANDLING_MODE_PEER());

        private final int value;

        ErrorHandlingMode(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }
}
