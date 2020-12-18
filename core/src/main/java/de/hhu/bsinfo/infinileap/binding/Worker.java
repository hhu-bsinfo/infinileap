package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;

public class Worker extends NativeObject {

    /* package-private */ Worker(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public static Worker create() {
        // ucp_worker_create
        return null;
    }
}
