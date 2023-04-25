package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import org.openucx.ucp_send_nbx_callback_t;

@FunctionalInterface
public interface SendCallback extends ucp_send_nbx_callback_t {

    void onRequestSent(long request, Status status, MemorySegment data);

    @Override
    default void apply(MemorySegment request, byte status, MemorySegment data) {
        onRequestSent(request.address(), Status.of(status), data);
    }

    default MemorySegment upcallSegment() {
        return ucp_send_nbx_callback_t.allocate(this, SegmentScope.auto());
    }
}
