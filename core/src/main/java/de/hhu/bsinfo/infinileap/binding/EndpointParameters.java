package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import org.openucx.ucx_h;
import org.openucx.ucx_h.ucp_ep_params_t;
import org.openucx.ucx_h.ucp_params_t;

public class EndpointParameters extends NativeObject {

    public EndpointParameters() {
        super(ucp_ep_params_t.allocate());
    }

    public EndpointParameters setRemoteAddress(WorkerAddress address) {
        ucp_ep_params_t.address$set(segment(), address.address());
        addFieldMask(Field.REMOTE_ADDRESS);
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
}
