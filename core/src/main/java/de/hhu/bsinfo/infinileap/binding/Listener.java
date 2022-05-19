package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ValueLayout;

import static org.openucx.OpenUcx.ucp_listener_destroy;
import static org.openucx.OpenUcx.ucp_listener_reject;

public class Listener extends NativeObject implements AutoCloseable {

    private final ListenerParameters parameters;

    /* package-private */ Listener(MemoryAddress address, ListenerParameters parameters) {
        super(address, ValueLayout.ADDRESS);
        this.parameters = parameters;
    }

    public void reject(ConnectionRequest connectionRequest) throws ControlException {
        var status = ucp_listener_reject(
                Parameter.of(this),
                connectionRequest.address()
        );

        if (Status.isNot(status, Status.OK)) {
            throw new ControlException(status);
        }
    }

    @Override
    public void close() {
        ucp_listener_destroy(address());
    }
}
