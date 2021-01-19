package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;

import static org.openucx.ucx_h.ucp_rkey_buffer_release;

public class RemoteKey extends NativeObject {

    /* package-private */ RemoteKey(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }
}
