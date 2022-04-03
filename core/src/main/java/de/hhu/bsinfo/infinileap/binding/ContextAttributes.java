package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import jdk.incubator.foreign.*;
import org.openucx.*;

import java.util.Optional;
import java.util.OptionalLong;

import static org.openucx.OpenUcx.*;

public class ContextAttributes extends NativeObject {

    ContextAttributes() {
        this(ResourceScope.newImplicitScope());
    }

    ContextAttributes(ResourceScope scope) {
        super(ucp_context_attr_t.allocate(scope));
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
