package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;

import static org.openucx.OpenUcx.ucp_listener_destroy;

public class Listener extends NativeObject implements AutoCloseable{

    /* package-private */ Listener(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    @Override
    public void close() {
        ucp_listener_destroy(address());
    }
}
