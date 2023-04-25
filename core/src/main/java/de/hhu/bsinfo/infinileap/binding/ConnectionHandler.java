package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import org.openucx.ucp_listener_conn_callback_t;

public abstract class ConnectionHandler {

    private final MemorySegment upcallSymbol;

    private final ucp_listener_conn_callback_t callback = (request, data) -> {
        onConnection(ConnectionRequest.of(request, data.address()));
    };

    public ConnectionHandler() {
        this.upcallSymbol = ucp_listener_conn_callback_t.allocate(callback, SegmentScope.auto());
    }

    public MemorySegment upcallSegment() {
        return upcallSymbol;
    }

    protected abstract void onConnection(ConnectionRequest request);
}
