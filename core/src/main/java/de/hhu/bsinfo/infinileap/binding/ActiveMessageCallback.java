package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.MemoryUtil;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucp_am_recv_callback_t;

public interface ActiveMessageCallback extends ucp_am_recv_callback_t {

    Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment data, MemoryAddress parameters);

    @Override
    default byte apply(MemoryAddress argument, MemoryAddress header, long headerSize, MemoryAddress data, long dataSize, MemoryAddress parameters) {
        return (byte) onActiveMessage(
                argument,
                MemoryUtil.wrap(header, headerSize),
                MemoryUtil.wrap(data, dataSize),
                parameters).value();
    }

    default MemoryAddress upcallStub() {
        return ucp_am_recv_callback_t.allocate(this);
    }
}
