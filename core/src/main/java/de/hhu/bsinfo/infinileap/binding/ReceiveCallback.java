package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;
import org.openucx.ucx_h.ucp_request_param_t;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@FunctionalInterface
public interface ReceiveCallback extends ucp_request_param_t.cb.recv {

    void onRequestReceived(Request request, Status status, MemoryAddress tagInfo, MemoryAddress data);

    @Override
    default void apply(MemoryAddress request, byte status, MemoryAddress tagInfo, MemoryAddress data) {
        onRequestReceived(Request.of(request), Status.of(status), tagInfo, data);
    }

    default MemorySegment upcallStub() {
        return ucp_request_param_t.cb.recv.allocate(this);
    }
}
