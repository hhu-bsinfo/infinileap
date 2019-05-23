package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryRegion {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryRegion.class);

    private final long handle;
    private final ByteBuffer byteBuffer;

    public MemoryRegion(long handle, ByteBuffer byteBuffer) {
        this.handle = handle;
        this.byteBuffer = byteBuffer;
    }

    long getHandle() {
        return handle;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public boolean deregister() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.deregisterMemoryRegion(handle, result.getHandle());
        if(result.isError()) {
            LOGGER.error("Could not deregister memory region");
            return false;
        }

        result.releaseInstance();
        return true;
    }
}
