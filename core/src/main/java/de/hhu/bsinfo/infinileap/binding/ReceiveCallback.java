package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h.ucp_tag_recv_nbx_callback_t;

// TODO(krakowski)
//  Fix parent interface name

@FunctionalInterface
public interface ReceiveCallback extends ucp_tag_recv_nbx_callback_t {

    void onRequestReceived(Request request, Status status, MemoryAddress tagInfo, MemoryAddress data);

    @Override
    default void apply(MemoryAddress request, byte status, MemoryAddress tagInfo, MemoryAddress data) {
        onRequestReceived(Request.of(request), Status.of(status), tagInfo, data);
    }

    default MemorySegment upcallStub() {
        return ucp_tag_recv_nbx_callback_t.allocate(this);
    }
}
