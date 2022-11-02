package de.hhu.bsinfo.infinileap.engine.buffer;

import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.agrona.hints.ThreadHints;

import java.lang.foreign.MemorySession;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.IntConsumer;

@Slf4j
public class DynamicBufferPool implements BufferPool {

    public static final int FLAG_PRIVATE = 0x80000000;

    /**
     * Pooled buffers stored using their identifiers as indices.
     */
    private PooledBuffer[] indexedBuffers;

    /**
     * Pooled buffers.
     */
    private final Queue<PooledBuffer> buffers;

    /**
     * This pool's associated resource scope.
     */
    private MemorySession session = MemorySession.openImplicit();

    public DynamicBufferPool(final int count, final long size) {
        indexedBuffers = new PooledBuffer[count];
        buffers = new LinkedList<>();

        // Create base buffer containing enough space for pooled buffers
        var base = MemoryUtil.allocate(count * size, MemoryAlignment.PAGE, session);

        IntConsumer releaser = this::release;
        for (int i = 0; i < count; i++) {

            var slice = base.asSlice(((long) i * size), size);

            // Create a new pooled buffer using the previously sliced chunk of memory
            indexedBuffers[i] = new PooledBuffer(i | FLAG_PRIVATE, slice, releaser);

            // Push the pooled buffer into our queue
            buffers.add(indexedBuffers[i]);
        }
    }

    public PooledBuffer claim() {
        if (buffers.isEmpty()) {
            grow();
        }

        return buffers.poll();
    }

    public PooledBuffer claim(int timeout) {
        return claim();
    }

    public void release(int identifier) {
        // Get buffer by identifier
        var buffer = indexedBuffers[identifier & ~FLAG_PRIVATE];

        // Put buffer back into queue
        while (!buffers.offer(buffer)) {
            ThreadHints.onSpinWait();
        }
    }

    public PooledBuffer get(int identifier) {
        return indexedBuffers[identifier & ~FLAG_PRIVATE];
    }

    public int count() {
        return indexedBuffers.length;
    }

    private void grow() {
        log.warn("Growing buffer");
        var currentCount = indexedBuffers.length;

        // Copy old content into new array
        var pooledBuffers = new PooledBuffer[currentCount * 2];
        System.arraycopy(indexedBuffers, 0, pooledBuffers, 0, currentCount);

        var byteSize = pooledBuffers[0].segment().byteSize();

        // Fill allocated array with new pooled buffers
        var base = MemoryUtil.allocate(currentCount * byteSize, MemoryAlignment.PAGE, session);
        IntConsumer releaser = this::release;
        for (int i = currentCount; i < pooledBuffers.length; i++) {

            var slice = base.asSlice(((long) (i - currentCount) * byteSize), byteSize);

            // Create a new pooled buffer using the previously sliced chunk of memory
            pooledBuffers[i] = new PooledBuffer(i | FLAG_PRIVATE, slice, releaser);

            // Push the pooled buffer into our queue
            buffers.add(pooledBuffers[i]);
        }

        // Update reference to new array
        indexedBuffers = pooledBuffers;
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
