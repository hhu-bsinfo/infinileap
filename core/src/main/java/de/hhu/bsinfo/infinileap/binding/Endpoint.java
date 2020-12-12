package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;

public class Endpoint extends NativeObject {

    private Endpoint(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public static Endpoint open() {
        // ucp_ep_create
        return null;
    }
}
