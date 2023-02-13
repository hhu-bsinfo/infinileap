package de.hhu.bsinfo.infinileap.common.buffer;

import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import org.agrona.BitUtil;
import org.agrona.concurrent.ringbuffer.RecordDescriptor;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static org.agrona.concurrent.broadcast.RecordDescriptor.PADDING_MSG_TYPE_ID;
import static org.agrona.concurrent.ringbuffer.RingBuffer.INSUFFICIENT_CAPACITY;


/**
 * A ring buffer used for storing requests.
 * This implementation is a modified version of {@link org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer}.
 */
public class ManyToOneRingBuffer {

    @FunctionalInterface
    public interface MessageHandler {
        void onMessage(int type, MemorySegment buffer, long index, int length);
    }

    private static final VarHandle LONG_HANDLE = MethodHandles.memorySegmentViewVarHandle(ValueLayout.JAVA_LONG);

    private static final VarHandle INT_HANDLE = MethodHandles.memorySegmentViewVarHandle(ValueLayout.JAVA_INT);

    /**
     * This buffer's maximum capacity in bytes.
     */
    private final int capacity;

    /**
     * The index within our backing buffer at which the head position is stored.
     */
    private final int headPositionIndex;

    /**
     * The index within our backing buffer at which the cached head position is stored.
     */
    private final int headCachePositionIndex;

    /**
     * The index within our backing buffer at which the tail position is stored.
     */
    private final int tailPositionIndex;

    /**
     * The underlying buffer used for storing data.
     */
    private final MemorySegment buffer;

    /**
     * This ring buffer's native base address.
     */
    private final long baseAddress;

    /**
     * Bitmask used to keep indices within the buffer's bounds.
     */
    private final int indexMask;

    private final MemorySession session = MemorySession.openImplicit();

    public ManyToOneRingBuffer(int size) {

        // Allocate a new page-aligned buffer
        buffer = MemoryUtil.allocateNative(size + RingBufferDescriptor.TRAILER_LENGTH, MemoryAlignment.PAGE, session);
        baseAddress = MemoryUtil.nativeAddress(buffer);

        // Store the buffer's actual capacity
        capacity = (int) buffer.byteSize() - RingBufferDescriptor.TRAILER_LENGTH;
        indexMask = capacity - 1;

        // Remember positions at which indices are stored
        headPositionIndex = capacity + RingBufferDescriptor.HEAD_POSITION_OFFSET;
        headCachePositionIndex = capacity + RingBufferDescriptor.HEAD_CACHE_POSITION_OFFSET;
        tailPositionIndex = capacity + RingBufferDescriptor.TAIL_POSITION_OFFSET;
    }

    public int read(final MessageHandler handler, final int limit) {

        // Keep track of the messages we already read
        var messagesRead = 0;

        // Retrieve our current position within the buffer
        final var buffer = this.buffer;
        final var headPositionIndex = this.headPositionIndex;
        final var head = (long) LONG_HANDLE.get(buffer, headPositionIndex);
        final var capacity = this.capacity;
        final var headIndex = (int) head & indexMask;
        final var maxBlockLength = capacity - headIndex;

        // Keep track of the number of bytes we read
        var bytesRead = 0;
        while ((bytesRead < maxBlockLength) && (messagesRead < limit)) {
            final var recordIndex = headIndex + bytesRead;
            final var recordLength = (int) INT_HANDLE.getVolatile(buffer, lengthOffset(recordIndex));

            // If this record wasn't commited yet, we have to abort
            if (recordLength <= 0) {
                break;
            }

            // Increment the number of bytes processed
            bytesRead += BitUtil.align(recordLength, RecordDescriptor.ALIGNMENT);

            // Skip this record if it represents padding
            final var messageTypeId = (int) INT_HANDLE.get(buffer, typeOffset(recordIndex));
            if (messageTypeId == PADDING_MSG_TYPE_ID) {
                continue;
            }

            handler.onMessage(messageTypeId, buffer, recordIndex + RecordDescriptor.HEADER_LENGTH, recordLength - RecordDescriptor.HEADER_LENGTH);
            messagesRead++;

            // Release record
//            INT_HANDLE.setVolatile(buffer, lengthOffset(recordIndex), 0);
        }

        if (bytesRead != 0) {
            buffer.asSlice(headIndex, bytesRead).fill((byte) 0);
            LONG_HANDLE.setRelease(buffer, headPositionIndex, head + bytesRead);
        }

        // Return the number of bytes read so the consumer can commit it later
        return messagesRead;
    }

    public long tryClaim(int type, final int bytes) {

        final var buffer = this.buffer;

        // Calculate the required size in bytes
        final var recordLength = bytes + RecordDescriptor.HEADER_LENGTH;

        // Claim the required space
        final var recordIndex = claim(buffer, recordLength);

        // Check if space was claimed sucessfully
        if (recordIndex == INSUFFICIENT_CAPACITY) {
            return INSUFFICIENT_CAPACITY;
        }

        // Block claimed space
        INT_HANDLE.setRelease(buffer, lengthOffset(recordIndex), -recordLength);
        VarHandle.releaseFence();
        INT_HANDLE.set(buffer, typeOffset(recordIndex), type);

        // Return the index at which the producer may write its request
        return encodedMsgOffset(recordIndex);
    }

    public void commit(final long index) {
        final var buffer = this.buffer;

        // Calculate the request index and length
        final long recordIndex = index - RecordDescriptor.HEADER_LENGTH;
        final int recordLength = (int) INT_HANDLE.get(buffer, lengthOffset(recordIndex));

        // Commit the request
        INT_HANDLE.setRelease(buffer, lengthOffset(recordIndex), -recordLength);
    }

    public MemorySegment buffer() {
        return buffer;
    }

    private long claim(final MemorySegment buffer, final int length) {

        // Calculate the required space to claim
        final var required = BitUtil.align(length, RecordDescriptor.ALIGNMENT);

        // This buffer's capacity
        final var total = capacity;

        // The index at which the tail position is stored
        final var tailPosition = tailPositionIndex;

        // The index at which the cached head position is stored
        final var headCachePosition = headCachePositionIndex;

        // Mask used to keep indices within bounds
        final var mask = indexMask;


        var head = (long) LONG_HANDLE.getVolatile(buffer, headCachePosition);
        long tail;
        int tailIndex;
        int padding;

        do {

            // Calculate available space using the cached head position
            tail = (long) LONG_HANDLE.getVolatile(buffer, tailPosition);
            final var available = total - (int) (tail - head);
            if (required > available) { // If the required size is less than the cached available space left

                // Calculate available space using the head position
                head = (long) LONG_HANDLE.getVolatile(buffer, headPositionIndex);
                if (required > (total - (int) (tail - head))) { // If the required size is less than the current available space left
                    return INSUFFICIENT_CAPACITY;
                }

                // Update the cached head position
                LONG_HANDLE.setRelease(buffer, headCachePosition, head);
            }

            // At this point we know that there is a chunk of
            // memory at least the size we requested

            // Try to acquire the required space
            padding = 0;
            tailIndex = (int) tail & mask;
            final var remaining = total - tailIndex;
            if (required > remaining) { // If the space between the tail and the upper bound is not sufficient

                // Wrap around the head index
                var headIndex = (int) head & mask;
                if (required > headIndex) {  // If there is not enough space at the beginning of our buffer

                    // Update our head index for one last try
                    head = (long) LONG_HANDLE.getVolatile(buffer, headPositionIndex);
                    headIndex = (int) head & mask;
                    if (required > headIndex) {
                        return INSUFFICIENT_CAPACITY;
                    }

                    // Update the cached head position
                    LONG_HANDLE.setRelease(buffer, headCachePosition, head);
                }

                padding = remaining;
            }
        } while(!LONG_HANDLE.compareAndSet(buffer, tailPosition, tail, tail + padding + required));

        if (padding != 0) {
            INT_HANDLE.setRelease(buffer, lengthOffset(tailIndex), -padding);
            VarHandle.releaseFence();
            INT_HANDLE.set(buffer, typeOffset(tailIndex), PADDING_MSG_TYPE_ID);
            INT_HANDLE.setRelease(buffer, lengthOffset(tailIndex), padding);

            // If there was padding at the end of the buffer
            // our claimed space starts at index 0
            tailIndex = 0;
        }

        return tailIndex;
    }

    public static long lengthOffset(final long recordOffset) {
        return recordOffset;
    }

    public static long typeOffset(final long recordOffset) {
        return recordOffset + BitUtil.SIZE_OF_INT;
    }

    public static long encodedMsgOffset(final long recordOffset) {
        return recordOffset + RecordDescriptor.HEADER_LENGTH;
    }
}
