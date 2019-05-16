package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.verbs.MemoryRegion.AccessFlag;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtectionDomain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtectionDomain.class);

    private final long handle;

    ProtectionDomain(long handle) {
        this.handle = handle;
    }

    long getHandle() {
        return handle;
    }

    public boolean deallocate() {
        var result = new Result();
        Verbs.deallocateProtectionDomain(handle, result.getHandle());

        if(result.isError()) {
            LOGGER.error("Could not deallocate protection domain");
            return false;
        }

        return true;
    }

    @Nullable
    public MemoryRegion registerMemoryRegion(int size, AccessFlag... flags) {
        return registerMemoryRegion(ByteBuffer.allocateDirect(size), flags);
    }

    @Nullable
    public MemoryRegion registerMemoryRegion(ByteBuffer buffer, AccessFlag... flags) {
        int access = 0;
        for(var flag : flags) {
            access |= flag.getValue();
        }

        var result = new Result();
        Verbs.registerMemoryRegion(handle, MemoryUtil.getAddress(buffer), buffer.capacity(), access, result.getHandle());

        if(result.isError()) {
            LOGGER.error("Could not register memory region");
            return null;
        }

        return new MemoryRegion(result.getResultHandle(), buffer);
    }
}
