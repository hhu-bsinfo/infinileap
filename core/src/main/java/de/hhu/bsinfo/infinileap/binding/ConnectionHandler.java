package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;
import org.openucx.ucx_h.ucp_listener_conn_handler_t;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@FunctionalInterface
public interface ConnectionHandler extends ucp_listener_conn_handler_t.cb$5 {

    void onConnection(ConnectionRequest request);

    @Override
    default void apply(MemoryAddress request, MemoryAddress data) {
        onConnection(ConnectionRequest.of(request, data.toRawLongValue()));
    }

    default MemorySegment upcallStub() {
        return ucp_listener_conn_handler_t.allocate();
    }
}
