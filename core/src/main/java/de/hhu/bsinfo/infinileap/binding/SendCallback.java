package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import org.openucx.ucp_send_nbx_callback_t;

@FunctionalInterface
public interface SendCallback extends ucp_send_nbx_callback_t {

    void onRequestSent(long request, Status status, MemoryAddress data);

    @Override
    default void apply(MemoryAddress request, byte status, MemoryAddress data) {
        onRequestSent(request.toRawLongValue(), Status.of(status), data);
    }

    default MemorySegment upcallStub() {
        return ucp_send_nbx_callback_t.allocate(this, MemorySession.openImplicit());
    }
}
