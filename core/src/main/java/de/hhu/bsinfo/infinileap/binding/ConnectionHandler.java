package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import org.openucx.ucp_listener_conn_callback_t;

public abstract class ConnectionHandler {

    private final MemorySegment upcallSymbol;

    private final ucp_listener_conn_callback_t callback = (request, data) -> {
        onConnection(ConnectionRequest.of(request, data.toRawLongValue()));
    };

    public ConnectionHandler() {
        this.upcallSymbol = ucp_listener_conn_callback_t.allocate(callback, MemorySession.openImplicit());
    }

    public MemoryAddress upcallAddress() {
        return upcallSymbol.address();
    }

    protected abstract void onConnection(ConnectionRequest request);
}
