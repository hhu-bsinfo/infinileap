package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import jdk.incubator.foreign.CLinker;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.openucx.ucx_h;
import org.openucx.ucx_h.ucp_params_t;

import java.lang.invoke.MethodHandle;

@Accessors(chain = true)
public class ContextParameters extends NativeObject {

    public ContextParameters() {
        super(ucp_params_t.allocate());
    }

    public long getFeatures() {
        return ucp_params_t.features$get(segment());
    }

    public ContextParameters setFeatures(Feature... features) {
        ucp_params_t.features$set(segment(), BitMask.longOf(features));
        addFieldMask(Field.FEATURES);
        return this;
    }

    public long getRequestSize() {
        return ucp_params_t.request_size$get(segment());
    }

    public ContextParameters setRequestSize(long size) {
        ucp_params_t.request_size$set(segment(), size);
        addFieldMask(Field.REQUEST_SIZE);
        return this;
    }

    private long getFieldMask() {
        return ucp_params_t.field_mask$get(segment());
    }

    private void setFieldMask(Field... fields) {
        ucp_params_t.field_mask$set(segment(), BitMask.longOf(fields));
    }

    private void addFieldMask(Field... fields) {
        ucp_params_t.field_mask$set(segment(), BitMask.longOf(fields) | getFieldMask());
    }

    public enum Field implements LongFlag {
        FEATURES(ucx_h.UCP_PARAM_FIELD_FEATURES()),
        REQUEST_SIZE(ucx_h.UCP_PARAM_FIELD_REQUEST_SIZE()),
        REQUEST_INIT(ucx_h.UCP_PARAM_FIELD_REQUEST_INIT ()),
        REQUEST_CLEANUP(ucx_h.UCP_PARAM_FIELD_REQUEST_CLEANUP()),
        TAG_SENDER_MASK(ucx_h.UCP_PARAM_FIELD_TAG_SENDER_MASK()),
        SHARED_WORKERS(ucx_h.UCP_PARAM_FIELD_MT_WORKERS_SHARED()),
        ESTIMATED_NUM_EPS(ucx_h.UCP_PARAM_FIELD_ESTIMATED_NUM_EPS()),
        ESTIMATED_NUM_PPN(ucx_h.UCP_PARAM_FIELD_ESTIMATED_NUM_PPN());

        private final long value;

        Field(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    public enum Feature implements LongFlag {
        TAG(ucx_h.UCP_FEATURE_TAG()),
        RMA(ucx_h.UCP_FEATURE_RMA()),
        ATOMIC_32(ucx_h.UCP_FEATURE_AMO32()),
        ATOMIC_64(ucx_h.UCP_FEATURE_AMO64()),
        WAKEUP(ucx_h.UCP_FEATURE_WAKEUP()),
        STREAM(ucx_h.UCP_FEATURE_STREAM()),
        AM(ucx_h.UCP_FEATURE_AM());

        private final long value;

        Feature(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }
}
