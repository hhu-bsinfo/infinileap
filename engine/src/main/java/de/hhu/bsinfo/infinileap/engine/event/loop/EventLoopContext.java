package de.hhu.bsinfo.infinileap.engine.event.loop;

import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.common.multiplex.EventFileDescriptor;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.flag.IntegerFlag;
import de.hhu.bsinfo.infinileap.engine.buffer.BufferPool;
import de.hhu.bsinfo.infinileap.engine.buffer.PooledBuffer;
import de.hhu.bsinfo.infinileap.engine.buffer.StaticBufferPool;
import lombok.Builder;
import lombok.Data;

import static de.hhu.bsinfo.infinileap.engine.buffer.DynamicBufferPool.FLAG_PRIVATE;

@Builder
public @Data class EventLoopContext {

    private static final int BUFFER_CLAIM_TIMEOUT = 100;

    private final Thread thread;

    private final BufferPool sharedPool;

    private final BufferPool privatePool;

    private final Worker worker;

    private final WorkerEventLoop loop;

    private final EventFileDescriptor loopNotifier;

    public final boolean isInEventloop() {
        return Thread.currentThread() == thread;
    }

    public PooledBuffer claimBuffer() {
        return isInEventloop() ? privatePool.claim(BUFFER_CLAIM_TIMEOUT) : sharedPool.claim(BUFFER_CLAIM_TIMEOUT);
    }

    public PooledBuffer getBuffer(int identifier) {
        return (identifier & FLAG_PRIVATE) == FLAG_PRIVATE ? privatePool.get(identifier) : sharedPool.get(identifier);
    }

}
