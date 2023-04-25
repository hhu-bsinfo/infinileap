package de.hhu.bsinfo.infinileap.binding;

import org.openucx.ucp_err_handler_cb_t;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;

public interface ErrorHandler extends ucp_err_handler_cb_t {

    void onError(MemorySegment userData, MemorySegment endpoint, Status status);

    @Override
    default void apply(MemorySegment userData, MemorySegment endpoint, byte status) {
        onError(userData, endpoint, Status.of(status));
    }

    default MemorySegment upcallSegment() {
        return ucp_err_handler_cb_t.allocate(this, SegmentScope.auto());
    }
}
