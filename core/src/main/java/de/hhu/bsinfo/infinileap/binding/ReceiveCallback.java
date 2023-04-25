package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import org.openucx.ucp_tag_recv_nbx_callback_t;

@FunctionalInterface
public interface ReceiveCallback extends ucp_tag_recv_nbx_callback_t {

    void onRequestReceived(long request, Status status, MemorySegment tagInfo, MemorySegment data);

    @Override
    default void apply(MemorySegment request, byte status, MemorySegment tagInfo, MemorySegment data) {
        onRequestReceived(request.address(), Status.of(status), tagInfo, data);
    }

    default MemorySegment upcallSegment() {
        return ucp_tag_recv_nbx_callback_t.allocate(this, SegmentScope.auto());
    }
}
