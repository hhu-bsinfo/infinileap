package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.NativeInetSocketAddress;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import jdk.incubator.foreign.ResourceScope;
import org.openucx.*;

import java.net.InetSocketAddress;

import static org.openucx.OpenUcx.*;

public class EndpointParameters extends NativeObject {

    private NativeInetSocketAddress remoteAddress;

    public EndpointParameters() {
        this(ResourceScope.newImplicitScope());
    }

    public EndpointParameters(ResourceScope scope) {
        super(ucp_ep_params_t.allocate(scope));
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
        NO_LOOPBACK(UCP_EP_PARAMS_FLAGS_NO_LOOPBACK());

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
