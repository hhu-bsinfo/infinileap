package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.verbs.QueuePair.InitialAttributes;
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
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.deallocateProtectionDomain(handle, result.getHandle());
        if(result.isError()) {
            LOGGER.error("Could not deallocate protection domain");
            return false;
        }

        result.releaseInstance();
        return true;
    }

    @Nullable
    public MemoryRegion registerMemoryRegion(int size, AccessFlag... flags) {
        return registerMemoryRegion(ByteBuffer.allocateDirect(size), flags);
    }

    @Nullable
    public MemoryRegion registerMemoryRegion(ByteBuffer buffer, AccessFlag... flags) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.registerMemoryRegion(handle, MemoryUtil.getAddress(buffer), buffer.capacity(),
            BitMask.of(flags), result.getHandle());

        if(result.isError()) {
            LOGGER.error("Could not register memory region");
            return null;
        }

        result.releaseInstance();
        return new MemoryRegion(result.longValue(), buffer);
    }

    @Nullable
    public SharedReceiveQueue createSharedReceiveQueue(
        SharedReceiveQueue.InitialAttributes initialAttributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createSharedReceiveQueue(handle, initialAttributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not create shared receive queue");
            return null;
        }

        result.releaseInstance();
        return result.get(SharedReceiveQueue::new);
    }

    @Nullable
    public QueuePair createQueuePair(InitialAttributes initialAttributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);
        Verbs.createQueuePair(handle, initialAttributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Could not create queue pair");
            return null;
        }

        result.releaseInstance();
        return result.get(QueuePair::new);
    }
}
