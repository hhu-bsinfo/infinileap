package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemorySegment;

public class MemoryRegion {

    private final MemoryHandle handle;

    private final MemorySegment segment;

    private final MemoryDescriptor descriptor;

    MemoryRegion(MemoryHandle handle, MemorySegment segment, MemoryDescriptor descriptor) {
        this.handle = handle;
        this.segment = segment;
        this.descriptor = descriptor;
    }

    public MemorySegment segment() {
        return segment;
    }

    public MemoryDescriptor descriptor() {
        return descriptor;
    }

    MemoryHandle handle() {
        return handle;
    }
}
