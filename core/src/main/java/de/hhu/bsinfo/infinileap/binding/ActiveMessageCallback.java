package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.MemoryUtil;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h.ucp_am_recv_callback_t;

public interface ActiveMessageCallback extends ucp_am_recv_callback_t {

    Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment data, MemoryAddress parameters);

    @Override
    default byte apply(MemoryAddress argument, MemoryAddress header, long headerSize, MemoryAddress data, long dataSize, MemoryAddress parameters) {
        try (var headerSegment = MemoryUtil.createSegment(header, headerSize);
             var dataSegment = MemoryUtil.createSegment(data, dataSize)) {
            return (byte) onActiveMessage(argument, headerSegment, dataSegment, parameters).value();
        }
    }

    default MemorySegment upcallStub() {
        return ucp_am_recv_callback_t.allocate(this);
    }
}
