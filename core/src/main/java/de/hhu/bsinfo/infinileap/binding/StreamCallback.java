package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import org.openucx.ucp_stream_recv_nbx_callback_t;

@FunctionalInterface
public interface StreamCallback extends ucp_stream_recv_nbx_callback_t {

    void onStreamReceived(Request request, Status status, long length, MemoryAddress data);

    @Override
    default void apply(MemoryAddress request, byte status, long length, MemoryAddress data) {
        onStreamReceived(Request.of(request.toRawLongValue()), Status.of(status), length, data);
    }

    default MemorySegment upcallStub() {
        return ucp_stream_recv_nbx_callback_t.allocate(this, MemorySession.openImplicit());
    }
}
