package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.MemoryUtil;
import jdk.incubator.foreign.*;
import org.openucx.ucp_am_recv_callback_t;

public abstract class ActiveMessageCallback {

    private final NativeSymbol upcallSymbol;

    protected ActiveMessageCallback() {
        this.upcallSymbol = ucp_am_recv_callback_t.allocate(callback, ResourceScope.newImplicitScope());
    }

    private final ucp_am_recv_callback_t callback = (argument, header, headerSize, data, dataSize, parameters) ->
            (byte) onActiveMessage(
                        argument,
                        MemoryUtil.wrap(header, headerSize),
                        MemoryUtil.wrap(data, dataSize),
                        parameters
            ).value();

    public MemoryAddress upcallAddress() {
        return upcallSymbol.address();
    }

    protected abstract Status onActiveMessage(MemoryAddress argument, MemorySegment header, MemorySegment data, MemoryAddress parameters);
}
