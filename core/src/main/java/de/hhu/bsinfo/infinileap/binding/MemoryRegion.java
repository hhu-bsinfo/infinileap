package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemorySegment;

public class MemoryRegion {

    /**
     * The ucp handle for this memory region.
     */
    private final MemoryHandle handle;

    /**
     * The segment used to read from and write to this memory region.
     */
    private final MemorySegment segment;

    /**
     * The descriptor used for sharing this region's access details with remote hosts.
     */
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
