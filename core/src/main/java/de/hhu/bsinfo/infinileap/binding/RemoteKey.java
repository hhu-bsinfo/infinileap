package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static org.openucx.OpenUcx.ucp_rkey_destroy;

public class RemoteKey extends NativeObject implements AutoCloseable {

    /* package-private */ RemoteKey(MemorySegment base) {
        super(base, ValueLayout.ADDRESS);
    }

    @Override
    public void close() {
        ucp_rkey_destroy(segment());
    }
}
