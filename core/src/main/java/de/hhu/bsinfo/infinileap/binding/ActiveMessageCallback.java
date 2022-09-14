package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import java.lang.foreign.*;
import org.openucx.ucp_am_recv_callback_t;

public abstract class ActiveMessageCallback {

    private final MemorySegment upcallSymbol;

    private static final MemorySegment NULL = MemorySegment.ofAddress(MemoryAddress.NULL, 0L, MemorySession.global());

    protected ActiveMessageCallback() {
        this.upcallSymbol = ucp_am_recv_callback_t.allocate(callback, MemorySession.openImplicit());
    }

    private final ucp_am_recv_callback_t callback = (argument, header, headerSize, data, dataSize, parameters) ->
            (byte) onActiveMessage(
                        argument,
                        headerSize == 0 ? NULL : MemoryUtil.wrap(header, headerSize),
                        dataSize == 0 ? NULL : MemoryUtil.wrap(data, dataSize),
                        parameters
            ).value();

    public MemoryAddress upcallAddress() {
        return upcallSymbol.address();
    }

    protected abstract Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment data, MemoryAddress parameters);
}
