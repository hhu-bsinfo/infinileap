package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import jdk.incubator.foreign.ResourceScope;
import org.openucx.*;

import static org.openucx.OpenUcx.*;
import static org.openucx.OpenUcx.UCP_WORKER_PARAM_FIELD_EVENTS;


public class WorkerParameters extends NativeObject {

    public WorkerParameters() {
        this(ResourceScope.newImplicitScope());
    }

    public WorkerParameters(ResourceScope scope) {
        super(ucp_worker_params_t.allocate(scope));
    }

    public WorkerParameters setThreadMode(ThreadMode threadMode) {
        ucp_worker_params_t.thread_mode$set(segment(), threadMode.value());
        addFieldMask(Field.THREAD_MODE);
        return this;
    }

    public WorkerParameters setClientId(long clientId) {
        ucp_worker_params_t.client_id$set(segment(), clientId);
        addFieldMask(Field.CLIENT_ID);
        return this;
    }

    private long getFieldMask() {
        return ucp_params_t.field_mask$get(segment());
    }

    private void setFieldMask(Field... fields) {
        ucp_worker_params_t.field_mask$set(segment(), BitMask.longOf(fields));
    }

    private void addFieldMask(Field... fields) {
        ucp_worker_params_t.field_mask$set(segment(), BitMask.longOf(fields) | getFieldMask());
    }

    public enum Field implements LongFlag {
        THREAD_MODE(UCP_WORKER_PARAM_FIELD_THREAD_MODE()),
        USER_DATA(UCP_WORKER_PARAM_FIELD_USER_DATA()),
        CPU_MASK(UCP_WORKER_PARAM_FIELD_CPU_MASK()),
        EVENT_FD(UCP_WORKER_PARAM_FIELD_EVENT_FD()),
        EVENTS(UCP_WORKER_PARAM_FIELD_EVENTS()),
        CLIENT_ID(UCP_WORKER_PARAM_FIELD_CLIENT_ID());

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
