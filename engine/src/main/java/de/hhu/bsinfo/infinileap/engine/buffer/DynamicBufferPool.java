package de.hhu.bsinfo.infinileap.engine.buffer;

import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.agrona.collections.IntArrayQueue;

import java.lang.foreign.MemorySession;
import java.util.function.IntConsumer;

@Slf4j
public class DynamicBufferPool implements BufferPool {

    public static final int FLAG_PRIVATE = 0x80000000;

    /**
     * Pooled buffers stored using their identifiers as indices.
     */
    private PooledBuffer[] indexedBuffers;

    /**
     * A queue storing indices belonging to free buffer instances.
     */
    private IntArrayQueue indexQueue;

    /**
     * The size of a single pooled buffer.
     */
    private final long baseSize;

    /**
     * This pool's associated resource scope.
     */
    private MemorySession session = MemorySession.openImplicit();

    public DynamicBufferPool(final int count, final long size) {
        indexedBuffers = new PooledBuffer[count];
        indexQueue = new IntArrayQueue();
        baseSize = size;

        // Create base buffer containing enough space for pooled buffers
        var base = MemoryUtil.allocateNative(count * size, MemoryAlignment.PAGE, session);

        IntConsumer releaser = this::release;
        for (int i = 0; i < count; i++) {

            var slice = base.asSlice(((long) i * size), size);

            // Create a new pooled buffer using the previously sliced chunk of memory
            indexedBuffers[i] = new PooledBuffer(i | FLAG_PRIVATE, slice, releaser);

            // Add queue element
            indexQueue.addInt(i);
        }
    }

    public PooledBuffer claim() {
        if (indexQueue.isEmpty()) {
            grow();
        }

        return indexedBuffers[indexQueue.pollInt()];
    }

    public PooledBuffer claim(int timeout) {
        return claim();
    }

    public void release(int identifier) {
        indexQueue.addInt(identifier & ~FLAG_PRIVATE);
    }

    public PooledBuffer get(int identifier) {
        return indexedBuffers[identifier & ~FLAG_PRIVATE];
    }

    public int count() {
        return indexedBuffers.length;
    }

    private void grow() {
        var startTime = System.nanoTime();
        var currentCount = indexedBuffers.length;
        log.warn("Growing buffer from {} to {}", (currentCount * baseSize)/1024, (currentCount * baseSize * 2)/1024);

        // Copy old content into new array
        var pooledBuffers = new PooledBuffer[currentCount * 2];
        System.arraycopy(indexedBuffers, 0, pooledBuffers, 0, currentCount);

        var byteSize = pooledBuffers[0].segment().byteSize();

        // Fill allocated array with new pooled buffers
        var base = MemoryUtil.allocateNative(currentCount * byteSize, MemoryAlignment.PAGE, session);
        IntConsumer releaser = this::release;
        for (int i = currentCount; i < pooledBuffers.length; i++) {

            var slice = base.asSlice(((long) (i - currentCount) * byteSize), byteSize);

            // Create a new pooled buffer using the previously sliced chunk of memory
            pooledBuffers[i] = new PooledBuffer(i | FLAG_PRIVATE, slice, releaser);

            // Push the pooled buffer into our queue
            indexQueue.addInt(i);
        }

        // Update reference to new array
        indexedBuffers = pooledBuffers;
        log.debug("Growing buffer took {}ms", (System.nanoTime() - startTime) / 1000 / 1000);
    }

    @Override
    public String toString() {
        var first = indexedBuffers[0];
        var last = indexedBuffers[indexedBuffers.length - 1];
        return String.format("BufferPool { region: [ 0x%08X , 0x%08X ] }",
                first.segment().address().toRawLongValue(),
                last.segment().address().toRawLongValue() + last.segment().byteSize());
    }
}
