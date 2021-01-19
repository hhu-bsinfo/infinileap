package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public class MemoryHandle extends NativeObject {

    private final MemoryAddress handle;

    private final MemoryDescriptor descriptor;

    /* package-private */ MemoryHandle(MemorySegment segment, MemoryAddress handle, MemoryDescriptor descriptor) {
        super(segment);
        this.handle = handle;
        this.descriptor = descriptor;
    }

    // TODO(krakowski)
    //  Think about more fitting names for methods

    public MemorySegment memory() {
        return segment();
    }

    public MemoryDescriptor descriptor() {
        return descriptor;
    }

    MemoryAddress handle() {
        return handle;
    }
}
