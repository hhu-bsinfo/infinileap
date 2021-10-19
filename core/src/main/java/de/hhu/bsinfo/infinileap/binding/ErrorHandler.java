package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;
import org.openucx.ucp_err_handler_cb_t;

public interface ErrorHandler extends ucp_err_handler_cb_t {

    void onError(MemoryAddress userData, MemoryAddress endpoint, Status status);

    @Override
    default void apply(MemoryAddress userData, MemoryAddress endpoint, byte status) {
        onError(userData, endpoint, Status.of(status));
    }

    // TODO(krakowski)
    //  Use implicit scope and store reference on callback instance to prevent garbage collector from cleaning it up.
    default MemoryAddress upcallStub() {
        return ucp_err_handler_cb_t.allocate(this, ResourceScope.globalScope()).address();
    }
}
