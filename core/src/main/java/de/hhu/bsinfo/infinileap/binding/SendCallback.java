package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import org.openucx.ucp_send_nbx_callback_t;

@FunctionalInterface
public interface SendCallback extends ucp_send_nbx_callback_t {

    void onRequestSent(long request, Status status, MemoryAddress data);

    @Override
    default void apply(MemoryAddress request, byte status, MemoryAddress data) {
        onRequestSent(request.toRawLongValue(), Status.of(status), data);
    }

    // TODO(krakowski)
    //  Use implicit scope and store reference on callback instance to prevent garbage collector from cleaning it up.
    default MemoryAddress upcallStub() {
        return ucp_send_nbx_callback_t.allocate(this, ResourceScope.globalScope()).address();
    }
}
