package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import org.openucx.ucp_tag_recv_nbx_callback_t;

@FunctionalInterface
public interface ReceiveCallback extends ucp_tag_recv_nbx_callback_t {

    void onRequestReceived(long request, Status status, MemoryAddress tagInfo, MemoryAddress data);

    @Override
    default void apply(MemoryAddress request, byte status, MemoryAddress tagInfo, MemoryAddress data) {
        onRequestReceived(request.toRawLongValue(), Status.of(status), tagInfo, data);
    }

    default MemorySegment upcallStub() {
        return ucp_tag_recv_nbx_callback_t.allocate(this, MemorySession.openImplicit());
    }
}
