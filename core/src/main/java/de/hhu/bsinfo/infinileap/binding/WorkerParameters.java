package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import org.openucx.ucx_h;
import org.openucx.ucx_h.ucp_worker_params_t;

public class WorkerParameters extends NativeObject {

    public WorkerParameters() {
        super(ucp_worker_params_t.allocate());
    }

    public WorkerParameters setThreadMode(ThreadMode threadMode) {
        ucp_worker_params_t.thread_mode$set(segment(), threadMode.value());
        addFieldMask(Field.THREAD_MODE);
        return this;
    }

    private long getFieldMask() {
        return ucx_h.ucp_params_t.field_mask$get(segment());
    }

    private void setFieldMask(Field... fields) {
        ucp_worker_params_t.field_mask$set(segment(), BitMask.longOf(fields));
    }

    private void addFieldMask(Field... fields) {
        ucp_worker_params_t.field_mask$set(segment(), BitMask.longOf(fields) | getFieldMask());
    }

    public enum Field implements LongFlag {
        THREAD_MODE(ucx_h.UCP_WORKER_PARAM_FIELD_THREAD_MODE()),
        USER_DATA(ucx_h.UCP_WORKER_PARAM_FIELD_USER_DATA()),
        CPU_MASK(ucx_h.UCP_WORKER_PARAM_FIELD_CPU_MASK()),
        EVENT_FD(ucx_h.UCP_WORKER_PARAM_FIELD_EVENT_FD()),
        EVENTS(ucx_h.UCP_WORKER_PARAM_FIELD_EVENTS());

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
