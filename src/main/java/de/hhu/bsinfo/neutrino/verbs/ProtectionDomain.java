package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.verbs.MemoryRegion.AccessFlag;
import de.hhu.bsinfo.neutrino.verbs.SharedReceiveQueue.InitialAttributes;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtectionDomain implements NativeObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtectionDomain.class);

    private final long handle;

    ProtectionDomain(long handle) {
        this.handle = handle;
    }

    @Override
    public long getHandle() {
        return handle;
    }

    public boolean deallocate() {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.deallocateProtectionDomain(handle, result.getHandle());
        if(result.isError()) {
            LOGGER.error("Could not deallocate protection domain");
            return false;
        }

        result.free();
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

        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.registerMemoryRegion(handle, MemoryUtil.getAddress(buffer), buffer.capacity(), access, result.getHandle());
        if(result.isError()) {
            LOGGER.error("Could not register memory region");
            return null;
        }

        result.free();
        return new MemoryRegion(result.getPointer(), buffer);
    }

    @Nullable
    public SharedReceiveQueue createSharedReceiveQueue(InitialAttributes initialAttributes) {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();

        Verbs.createSharedReceiveQueue(handle, initialAttributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not create shared receive queue");
            return null;
        }

        result.free();
        return result.get(SharedReceiveQueue::new);
    }

    @Nullable QueuePair createQueuePair(QueuePair.Attributes attributes) {
        var result = (Result) Verbs.getNativeObjectPool(Result.class).newInstance();
        Verbs.createQueuePair(handle, attributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not create queue pair");
            return null;
        }

        result.free();
        return result.get(QueuePair::new);
    }
}
