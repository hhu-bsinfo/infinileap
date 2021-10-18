package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ValueLayout;

import static org.openucx.OpenUcx.ucp_listener_destroy;

public class Listener extends NativeObject implements AutoCloseable{

    /* package-private */ Listener(MemoryAddress address) {
        super(address, ValueLayout.ADDRESS);
    }

    @Override
    public void close() {
        ucp_listener_destroy(address());
    }
}
