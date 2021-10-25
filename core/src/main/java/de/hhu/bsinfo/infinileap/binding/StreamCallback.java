package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.NativeSymbol;
import jdk.incubator.foreign.ResourceScope;
import org.openucx.ucp_stream_recv_nbx_callback_t;

@FunctionalInterface
public interface StreamCallback extends ucp_stream_recv_nbx_callback_t {

    void onStreamReceived(Request request, Status status, long length, MemoryAddress data);

    @Override
    default void apply(MemoryAddress request, byte status, long length, MemoryAddress data) {
        onStreamReceived(Request.of(request.toRawLongValue()), Status.of(status), length, data);
    }

    default NativeSymbol upcallStub() {
        return ucp_stream_recv_nbx_callback_t.allocate(this, ResourceScope.newImplicitScope());
    }
}
