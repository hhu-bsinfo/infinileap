package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.Result;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryRegion {

    public enum AccessFlag {
        LOCAL_WRITE(1), REMOTE_WRITE(1 << 1), REMOTE_READ(1 << 2),
        REMOTE_ATOMIC(1 << 3), MW_BIND(1 << 4), ZERO_BASED(1 << 5),
        ON_DEMAND(1 << 6);

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
                    return LOCAL_WRITE;
                case 1 << 1:
                    return REMOTE_WRITE;
                case 1 << 2:
                    return REMOTE_READ;
                case 1 << 3:
                    return REMOTE_ATOMIC;
                case 1 << 4:
                    return MW_BIND;
                case 1 << 5:
                    return ZERO_BASED;
                case 1 << 6:
                    return ON_DEMAND;
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
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.deregisterMemoryRegion(handle, result.getHandle());
        if(result.isError()) {
            LOGGER.error("Could not deregister memory region");
            return false;
        }

        result.free();
        return true;
    }
}
