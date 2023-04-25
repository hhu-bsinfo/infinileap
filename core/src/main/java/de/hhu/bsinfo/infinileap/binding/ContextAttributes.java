package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;
import java.lang.foreign.*;
import org.openucx.*;

import static org.openucx.OpenUcx.*;

public class ContextAttributes extends NativeObject {

    ContextAttributes() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    ContextAttributes(SegmentAllocator allocator) {
        super(ucp_context_attr_t.allocate(allocator));
    }

    public long requestSize() {
        return ucp_context_attr_t.request_size$get(segment());
    }

    public ThreadMode threadMode() {
        return ThreadMode.from(ucp_context_attr_t.thread_mode$get(segment()));
    }

    void setFields(Field... fields) {
        ucp_context_attr_t.field_mask$set(segment(), BitMask.longOf(fields));
    }

    public enum Field implements LongFlag {
        REQUEST_SIZE(UCP_ATTR_FIELD_REQUEST_SIZE()),
        THREAD_MODE(UCP_ATTR_FIELD_THREAD_MODE());

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
