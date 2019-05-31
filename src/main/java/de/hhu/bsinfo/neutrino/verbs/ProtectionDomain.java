package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.verbs.QueuePair.InitialAttributes;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LinkNative("ibv_pd")
public class ProtectionDomain extends Struct implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtectionDomain.class);

    private final Context context = referenceField("context", Context::new);

    ProtectionDomain(long handle) {
        super(handle);
    }

    @Nullable
    public MemoryRegion registerMemoryRegion(int size, AccessFlag... flags) {
        return registerMemoryRegion(ByteBuffer.allocateDirect(size), flags);
    }

    @Nullable
    public MemoryRegion registerMemoryRegion(ByteBuffer buffer, AccessFlag... flags) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.registerMemoryRegion(getHandle(), MemoryUtil.getAddress(buffer), buffer.capacity(),
            BitMask.of(flags), result.getHandle());

        MemoryRegion ret = null;
        if(result.isError()) {
            LOGGER.error("Registering memory region failed with error [{}]", result.getStatus());
        } else {
            ret = new MemoryRegion(result.longValue(), buffer);
        }

        result.releaseInstance();

        return ret;
    }

    @Nullable
    public SharedReceiveQueue createSharedReceiveQueue(SharedReceiveQueue.InitialAttributes initialAttributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createSharedReceiveQueue(getHandle(), initialAttributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating shared receive queue failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(SharedReceiveQueue::new);
    }

    @Nullable
    public QueuePair createQueuePair(InitialAttributes initialAttributes) {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.createQueuePair(getHandle(), initialAttributes.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Creating queue pair failed with error [{}]", result.getStatus());
        }

        return result.getAndRelease(QueuePair::new);
    }

    @Override
    public void close() {
        var result = (Result) Verbs.getPoolableInstance(Result.class);

        Verbs.deallocateProtectionDomain(getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Closing device failed with error [{}]", result.getStatus());
        }

        result.releaseInstance();
    }
}
