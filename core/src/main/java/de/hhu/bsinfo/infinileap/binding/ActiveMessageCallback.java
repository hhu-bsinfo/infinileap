package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import java.lang.foreign.*;
import org.openucx.ucp_am_recv_callback_t;

public abstract class ActiveMessageCallback {

    private final MemorySegment upcallSymbol;

    protected ActiveMessageCallback() {
        this.upcallSymbol = ucp_am_recv_callback_t.allocate(callback, SegmentScope.auto());
    }

    private final ucp_am_recv_callback_t callback = (argument, header, headerSize, data, dataSize, parameters) ->
            (byte) onActiveMessage(
                        argument,
                        headerSize == 0 ? MemorySegment.NULL : MemoryUtil.wrap(header.address(), headerSize),
                        dataSize == 0 ? MemorySegment.NULL : MemoryUtil.wrap(data.address(), dataSize),
                        parameters
            ).value();

    public MemorySegment upcallSegment() {
        return upcallSymbol;
    }

    protected abstract Status onActiveMessage(MemorySegment argument, MemorySegment header, MemorySegment data, MemorySegment parameters);
}
