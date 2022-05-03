package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ValueLayout;

import static org.openucx.OpenUcx.ucp_rkey_destroy;

public class RemoteKey extends NativeObject implements AutoCloseable {

    /* package-private */ RemoteKey(MemoryAddress address) {
        super(address, ValueLayout.ADDRESS);
    }

    @Override
    public void close() {
        ucp_rkey_destroy(address());
    }
}
