package de.hhu.bsinfo.infinileap.engine.util;

import de.hhu.bsinfo.infinileap.binding.DataType;
import de.hhu.bsinfo.infinileap.binding.RequestParameters;
import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;
import org.agrona.concurrent.QueuedPipe;
import org.agrona.hints.ThreadHints;

import java.util.function.IntConsumer;

public class BufferPool {

    /**
     * The buffer used for creating pooled buffer instances.
     */
    private final MemorySegment baseBuffer;

    /**
     * Pooled buffers stored using their identifiers as indices.
     */
    private final PooledBuffer[] indexedBuffers;

    /**
     * Pooled buffers.
     */
    private final QueuedPipe<PooledBuffer> buffers;

    /**
     * This pool's associated resource scope.
     */
    private final ResourceScope scope = ResourceScope.newImplicitScope();

    public BufferPool(final int count, final long size) {
        indexedBuffers = new PooledBuffer[count];
        buffers = new ManyToManyConcurrentArrayQueue<>(count);

        // Create base buffer containing enough space for pooled buffers
        // and register it with the InfiniBand hardware
        baseBuffer = MemoryUtil.allocate(count * size, MemoryAlignment.PAGE, scope);

        IntConsumer releaser = this::release;
        for (int i = 0; i < count; i++) {

            var slice = baseBuffer.asSlice(((long) i * size), size);

            // Create a new pooled buffer using the previously sliced chunk of memory
            indexedBuffers[i] = new PooledBuffer(i, slice, releaser);

            // Push the pooled buffer into our queue
            buffers.add(indexedBuffers[i]);
        }
    }

    public PooledBuffer claim() {

        // Create variable for spin-wait loop
        PooledBuffer tmp;

        // Busy-wait until we get an buffer
        while ((tmp = buffers.poll()) == null) {
            ThreadHints.onSpinWait();
        }

        // Return the claimed buffer
        return tmp;
    }

    public void release(int identifier) {
        // Get buffer by identifier
        var buffer = indexedBuffers[identifier];

        // Put buffer back into queue
        while (!buffers.offer(buffer)) {
            ThreadHints.onSpinWait();
        }
    }

    public PooledBuffer get(int identifier) {
        return indexedBuffers[identifier];
    }

    @Override
    public String toString() {
        var first = indexedBuffers[0];
        var last = indexedBuffers[indexedBuffers.length - 1];
        return String.format("BufferPool { region: [ 0x%08X , 0x%08X ] }",
                first.segment.address().toRawLongValue(),
                last.segment.address().toRawLongValue() + last.segment.byteSize());
    }

    public static final class PooledBuffer {

        /**
         * This pooled buffer's backing memory segment slice.
         */
        private final MemorySegment segment;

        /**
         * This pooled buffer's identifier.
         */
        private final int identifier;

        /**
         * Function for returning this pooled buffer to its pool.
         */
        private final IntConsumer releaser;

        /**
         * The request parameters belonging to this buffer slice.
         */
        private final RequestParameters requestParameters = new RequestParameters()
                .setDataType(DataType.CONTIGUOUS_8_BIT)
                .setFlags(RequestParameters.Flag.ACTIVE_MESSAGE_REPLY);

        public PooledBuffer(int identifier, MemorySegment segment, IntConsumer releaser) {
            this.identifier = identifier;
            this.releaser = releaser;
            this.segment = segment;
        }

        public MemorySegment segment() {
            return segment;
        }

        public int identifier() {
            return identifier;
        }

        public RequestParameters requestParameters() {
            return requestParameters;
        }

        public void release() {
            releaser.accept(identifier);
        }
    }
}
