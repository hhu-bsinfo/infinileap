package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.binding.util.Status;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;

public class Context extends NativeObject {

    private Context(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public static Context initialize() {
        // ucp_init
        return null;
    }
}
