package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import org.openucx.ucp_stream_recv_nbx_callback_t;

@FunctionalInterface
public interface StreamCallback extends ucp_stream_recv_nbx_callback_t {

    void onStreamReceived(Request request, Status status, long length, MemorySegment data);

    @Override
    default void apply(MemorySegment request, byte status, long length, MemorySegment data) {
        onStreamReceived(Request.of(request.address()), Status.of(status), length, data);
    }

    default MemorySegment upcallSegment() {
        return ucp_stream_recv_nbx_callback_t.allocate(this, SegmentScope.auto());
    }
}
