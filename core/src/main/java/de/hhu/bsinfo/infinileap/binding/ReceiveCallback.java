package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h.ucp_tag_recv_nbx_callback_t;

@FunctionalInterface
public interface ReceiveCallback extends ucp_tag_recv_nbx_callback_t {

    void onRequestReceived(long request, Status status, MemoryAddress tagInfo, MemoryAddress data);

    @Override
    default void apply(MemoryAddress request, byte status, MemoryAddress tagInfo, MemoryAddress data) {
        onRequestReceived(request.toRawLongValue(), Status.of(status), tagInfo, data);
    }

    default MemorySegment upcallStub() {
        return ucp_tag_recv_nbx_callback_t.allocate(this);
    }
}
