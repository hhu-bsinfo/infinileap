package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.NativeInetSocketAddress;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import jdk.incubator.foreign.NativeScope;
import org.openucx.ucx_h;
import org.openucx.ucx_h.ucp_ep_params_t;
import org.openucx.ucx_h.ucp_params_t;
import org.openucx.ucx_h.ucs_sock_addr_t;

import java.net.InetSocketAddress;

public class EndpointParameters extends NativeObject {

    private NativeInetSocketAddress remoteAddress;

    public EndpointParameters() {
        super(ucp_ep_params_t.allocate());
    }

    public EndpointParameters setRemoteAddress(WorkerAddress address) {
        ucp_ep_params_t.address$set(segment(), address.address());
        addFieldMask(Field.REMOTE_ADDRESS);
        return this;
    }

    public EndpointParameters setRemoteAddress(InetSocketAddress socketAddress) {
        this.remoteAddress = NativeInetSocketAddress.convert(socketAddress);
        var sockaddr = ucp_ep_params_t.sockaddr$slice(segment());
        ucs_sock_addr_t.addr$set(sockaddr, remoteAddress.address());
        ucs_sock_addr_t.addrlen$set(sockaddr, remoteAddress.getLength());

        addFieldMask(Field.SOCK_ADDR);
        setFlags(Flag.CLIENT_SERVER);
        return this;
    }

    public EndpointParameters setConnectionRequest(ConnectionRequest request) {
        ucp_ep_params_t.conn_request$set(segment(), request.address());
        addFieldMask(Field.CONN_REQUEST);
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

    private void setFlags(Flag... flags) {
        ucp_ep_params_t.flags$set(segment(), BitMask.intOf(flags));
        addFieldMask(Field.FLAGS);
    }

    public enum Field implements LongFlag {
        REMOTE_ADDRESS(ucx_h.UCP_EP_PARAM_FIELD_REMOTE_ADDRESS()),
        CONN_REQUEST(ucx_h.UCP_EP_PARAM_FIELD_CONN_REQUEST()),
        ERR_HANDLER(ucx_h.UCP_EP_PARAM_FIELD_ERR_HANDLER()),
        ERR_HANDLING_MODE(ucx_h.UCP_EP_PARAM_FIELD_ERR_HANDLING_MODE()),
        SOCK_ADDR(ucx_h.UCP_EP_PARAM_FIELD_SOCK_ADDR()),
        USER_DATA(ucx_h.UCP_EP_PARAM_FIELD_USER_DATA()),
        FLAGS(ucx_h.UCP_EP_PARAM_FIELD_FLAGS());

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
        CLIENT_SERVER(ucx_h.UCP_EP_PARAMS_FLAGS_CLIENT_SERVER()),
        NO_LOOPBACK(ucx_h.UCP_EP_PARAMS_FLAGS_NO_LOOPBACK());

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
