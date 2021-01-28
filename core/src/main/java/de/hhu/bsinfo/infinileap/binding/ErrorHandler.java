package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;
import org.openucx.ucx_h.ucp_err_handler_t;

public interface ErrorHandler extends ucp_err_handler_t.cb$4 {

    void onError(MemoryAddress userData, MemoryAddress endpoint, Status status);

    @Override
    default void apply(MemoryAddress userData, MemoryAddress endpoint, byte status) {
        onError(userData, endpoint, Status.of(status));
    }

    default MemorySegment upcallStub() {
        return ucp_err_handler_t.cb$4.allocate(this);
    }
}
