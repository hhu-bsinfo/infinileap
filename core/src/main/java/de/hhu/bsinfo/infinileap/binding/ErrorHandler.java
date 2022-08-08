package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import org.openucx.ucp_err_handler_cb_t;

public interface ErrorHandler extends ucp_err_handler_cb_t {

    void onError(MemoryAddress userData, MemoryAddress endpoint, Status status);

    @Override
    default void apply(MemoryAddress userData, MemoryAddress endpoint, byte status) {
        onError(userData, endpoint, Status.of(status));
    }

    default MemorySegment upcallStub() {
        return ucp_err_handler_cb_t.allocate(this, MemorySession.openImplicit());
    }
}
