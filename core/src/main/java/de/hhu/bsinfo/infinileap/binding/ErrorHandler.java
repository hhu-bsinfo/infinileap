package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import org.openucx.ucp_err_handler_cb_t;

public interface ErrorHandler extends ucp_err_handler_cb_t {

    void onError(MemoryAddress userData, MemoryAddress endpoint, Status status);

    @Override
    default void apply(MemoryAddress userData, MemoryAddress endpoint, byte status) {
        onError(userData, endpoint, Status.of(status));
    }

    default MemoryAddress upcallStub() {
        return ucp_err_handler_cb_t.allocate(this);
    }
}
