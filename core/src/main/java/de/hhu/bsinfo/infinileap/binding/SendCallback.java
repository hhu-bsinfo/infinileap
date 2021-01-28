package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@FunctionalInterface
public interface SendCallback extends ucx_h.ucp_request_param_t.cb.send {

    void onRequestSent(Request request, Status status, MemoryAddress data);

    @Override
    default void apply(MemoryAddress request, byte status, MemoryAddress data) {
        onRequestSent(Request.of(request), Status.of(status), data);
    }

    default MemorySegment upcallStub() {
        return ucx_h.ucp_request_param_t.cb.send.allocate(this);
    }
}
