package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.NativeSymbol;
import jdk.incubator.foreign.ResourceScope;
import org.openucx.ucp_listener_conn_callback_t;

public abstract class ConnectionHandler {

    private final NativeSymbol upcallSymbol;

    private final ucp_listener_conn_callback_t callback = (request, data) -> {
        onConnection(ConnectionRequest.of(request, data.toRawLongValue()));
    };

    public ConnectionHandler() {
        this.upcallSymbol = ucp_listener_conn_callback_t.allocate(callback, ResourceScope.newImplicitScope());
    }

    public MemoryAddress upcallAddress() {
        return upcallSymbol.address();
    }

    protected abstract void onConnection(ConnectionRequest request);
}
