package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public class MemoryHandle extends NativeObject {

    private final MemoryAddress handle;

    /* package-private */ MemoryHandle(MemorySegment segment, MemoryAddress handle) {
        super(segment);
        this.handle = handle;
    }

    // TODO(krakowski)
    //  Think about more fitting names for methods

    public MemorySegment memory() {
        return segment();
    }

    MemoryAddress handle() {
        return handle;
    }
}
