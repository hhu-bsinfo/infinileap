package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;

import static org.openucx.OpenUcx.ucp_rkey_destroy;

public class RemoteKey extends NativeObject implements AutoCloseable {

    /* package-private */ RemoteKey(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    @Override
    public void close() {
        ucp_rkey_destroy(address());
    }
}
