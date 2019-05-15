package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.Result;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryRegion {

    public enum AccessFlag {
        IBV_ACCESS_LOCAL_WRITE(1), IBV_ACCESS_REMOTE_WRITE(1 << 1), IBV_ACCESS_REMOTE_READ(1 << 2),
        IBV_ACCESS_REMOTE_ATOMIC(1 << 3), IBV_ACCESS_MW_BIND(1 << 4), IBV_ACCESS_ZERO_BASED(1 << 5),
        IBV_ACCESS_ON_DEMAND(1 << 6);

        private final int value;

        AccessFlag(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static AccessFlag valueOf(int flag) {
            switch (flag) {
                case 1:
                    return IBV_ACCESS_LOCAL_WRITE;
                case 1 << 1:
                    return IBV_ACCESS_REMOTE_READ;
                case 1 << 2:
                    return IBV_ACCESS_REMOTE_READ;
                case 1 << 3:
                    return IBV_ACCESS_REMOTE_READ;
                case 1 << 4:
                    return IBV_ACCESS_REMOTE_READ;
                case 1 << 5:
                    return IBV_ACCESS_REMOTE_READ;
                case 1 << 6:
                    return IBV_ACCESS_ON_DEMAND;
                default:
                    throw new IllegalArgumentException(String.format("Unkown operation code provided %d", flag));
            }
        }
    }

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
        var result = new Result();
        Verbs.deregisterMemoryRegion(handle, result.getHandle());

        if(result.isError()) {
            LOGGER.error("Could not deregister memory region");
            return false;
        }

        return true;
    }
}
