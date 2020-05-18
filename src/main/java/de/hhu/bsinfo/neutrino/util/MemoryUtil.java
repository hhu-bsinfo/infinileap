package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class MemoryUtil {

    public static boolean contains(MemoryRegion region, DirectBuffer buffer) {

        // Wrapped byte[] are not supported
        if (buffer.byteArray() != null) {
            return false;
        }

        // Check if lower bound is within memory region
        if (buffer.addressOffset() < region.getAddress()) {
            return false;
        }

        // Check if upper bound is within memory region
        return buffer.addressOffset() + buffer.capacity() <= region.getAddress() + region.getLength();
    }

    public static AtomicBuffer allocateAligned(int size, MemoryAlignment alignment) {
        return new UnsafeBuffer(BufferUtil.allocateDirectAligned(size, alignment.value()));
    }

    public static AtomicBuffer wrap(long address, int size) {
        return new UnsafeBuffer(address, size);
    }
}
