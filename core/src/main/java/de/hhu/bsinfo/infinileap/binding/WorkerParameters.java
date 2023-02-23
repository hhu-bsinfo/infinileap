package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.flag.IntegerFlag;
import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;

import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import org.openucx.*;

import static org.openucx.OpenUcx.*;
import static org.openucx.OpenUcx.UCP_WORKER_PARAM_FIELD_EVENTS;


public class WorkerParameters extends NativeObject {

    public WorkerParameters() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public WorkerParameters(SegmentAllocator allocator) {
        super(ucp_worker_params_t.allocate(allocator));
    }

    public WorkerParameters setThreadMode(ThreadMode threadMode) {
        ucp_worker_params_t.thread_mode$set(segment(), threadMode.value());
        addFieldMask(Field.THREAD_MODE);
        return this;
    }

    public WorkerParameters setWakeupEvents(WakeupEvent... events) {
        ucp_worker_params_t.events$set(segment(), BitMask.intOf(events));
        addFieldMask(Field.EVENTS);
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

    public enum WakeupEvent implements IntegerFlag {
        REMOTE_MEMORY_ACCESS(UCP_WAKEUP_RMA()),
        ATOMIC_OPERATION(UCP_WAKEUP_AMO()),
        TAG_SEND(UCP_WAKEUP_TAG_SEND()),
        TAG_RECEIVE(UCP_WAKEUP_TAG_RECV()),
        TX(UCP_WAKEUP_TX()),
        RX(UCP_WAKEUP_RX()),
        EDGE(UCP_WAKEUP_EDGE());

        private final int value;

        WakeupEvent(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
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
