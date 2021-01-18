package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import lombok.experimental.Accessors;
import org.openucx.ucx_h.*;

import static org.openucx.ucx_h.*;


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
        FEATURES(UCP_PARAM_FIELD_FEATURES()),
        REQUEST_SIZE(UCP_PARAM_FIELD_REQUEST_SIZE()),
        REQUEST_INIT(UCP_PARAM_FIELD_REQUEST_INIT ()),
        REQUEST_CLEANUP(UCP_PARAM_FIELD_REQUEST_CLEANUP()),
        TAG_SENDER_MASK(UCP_PARAM_FIELD_TAG_SENDER_MASK()),
        SHARED_WORKERS(UCP_PARAM_FIELD_MT_WORKERS_SHARED()),
        ESTIMATED_NUM_EPS(UCP_PARAM_FIELD_ESTIMATED_NUM_EPS()),
        ESTIMATED_NUM_PPN(UCP_PARAM_FIELD_ESTIMATED_NUM_PPN());

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
        TAG(UCP_FEATURE_TAG()),
        RMA(UCP_FEATURE_RMA()),
        ATOMIC_32(UCP_FEATURE_AMO32()),
        ATOMIC_64(UCP_FEATURE_AMO64()),
        WAKEUP(UCP_FEATURE_WAKEUP()),
        STREAM(UCP_FEATURE_STREAM()),
        AM(UCP_FEATURE_AM());

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
