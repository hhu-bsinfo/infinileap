package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import org.openucx.ucp_listener_conn_callback_t;

@FunctionalInterface
public interface ConnectionHandler extends ucp_listener_conn_callback_t {

    void onConnection(ConnectionRequest request);

    @Override
    default void apply(MemoryAddress request, MemoryAddress data) {
        onConnection(ConnectionRequest.of(request, data.toRawLongValue()));
    }

    // TODO(krakowski)
    //  Use implicit scope and store reference on callback instance to prevent garbage collector from cleaning it up.
    default MemoryAddress upcallStub() {
        return ucp_listener_conn_callback_t.allocate(this, ResourceScope.globalScope()).address();
    }
}
