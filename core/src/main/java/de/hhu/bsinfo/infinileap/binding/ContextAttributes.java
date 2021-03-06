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

    public OptionalLong requestSize() {
        if (hasField(Field.REQUEST_SIZE)) {
            return OptionalLong.of(ucp_context_attr_t.request_size$get(segment()));
        }

        return OptionalLong.empty();
    }

    public Optional<ThreadMode> threadMode() {
        if (hasField(Field.THREAD_MODE)) {
            return Optional.of(ThreadMode.from(ucp_context_attr_t.thread_mode$get(segment())));
        }

        return Optional.empty();
    }

    private boolean hasField(Field field) {
        return BitMask.isSet(ucp_context_attr_t.field_mask$get(segment()), field);
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
