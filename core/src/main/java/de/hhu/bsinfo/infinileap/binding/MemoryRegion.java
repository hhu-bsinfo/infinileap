package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.CloseException;
import java.lang.foreign.MemorySegment;

import static org.openucx.OpenUcx.*;

public class MemoryRegion implements AutoCloseable {

    /**
     * The context this region is associated with.
     */
    private final Context context;

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

    MemoryRegion(Context context, MemoryHandle handle, MemorySegment segment, MemoryDescriptor descriptor) {
        this.context = context;
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

    @Override
    public void close() throws CloseException {
        var status = ucp_mem_unmap(context.address(), handle.address());
        if (Status.isNot(status, Status.OK)) {
            throw new CloseException(new ControlException(status));
        }
    }
}
