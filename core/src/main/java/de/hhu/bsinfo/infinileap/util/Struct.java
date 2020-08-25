package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import org.linux.rdma.RuntimeHelper;

import java.nio.ByteBuffer;

@Slf4j
public class Struct implements MemorySegment {

    /**
     * The {@link MemorySegment} used to interact with memory
     * obtained through native function calls.
     */
    private static final MemorySegment BASE = MemorySegment.ofNativeRestricted(MemoryAddress.NULL, Long.MAX_VALUE, null, null, null)
            .withAccessModes(READ | WRITE | CLOSE);

    /**
     * This struct's backing memory segment.
     */
    private final MemorySegment segment;

    /**
     * This struct's base address within its segment.
     */
    private final MemoryAddress baseAddress;

    protected Struct(MemoryAddress address, MemoryLayout layout) {
        // Since accessing memory obtained from native functions is
        // considered dangerous, we need to create a restricted
        // MemorySegment first by using our base segment.
        this(MemorySegment.ofNativeRestricted(address, layout.byteSize(), null, null, null));
    }

    protected Struct(MemorySegment segment) {
        if (segment.address().equals(MemoryAddress.NULL)) {
            throw new IllegalArgumentException("memory address is pointing at null");
        }

        if (!segment.isAlive()) {
            throw new IllegalArgumentException("the provided segment must be alive");
        }

        this.segment = segment;
        baseAddress = segment.address();
    }

    @Override
    public void close() {
        try {
            segment.close();
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public MemoryAddress address() {
        return segment.address();
    }

    @Override
    public Thread ownerThread() {
        return segment.ownerThread();
    }

    @Override
    public MemorySegment withOwnerThread(Thread newOwner) {
        return segment.withOwnerThread(newOwner);
    }

    @Override
    public long byteSize() {
        return segment.byteSize();
    }

    @Override
    public MemorySegment withAccessModes(int accessModes) {
        return segment.withAccessModes(accessModes);
    }

    @Override
    public boolean hasAccessModes(int accessModes) {
        return segment.hasAccessModes(accessModes);
    }

    @Override
    public int accessModes() {
        return segment.accessModes();
    }

    @Override
    public MemorySegment asSlice(long offset, long newSize) {
        return segment.asSlice(offset, newSize);
    }

    @Override
    public boolean isAlive() {
        return segment.isAlive();
    }

    @Override
    public MemorySegment fill(byte value) {
        return segment.fill(value);
    }

    @Override
    public void copyFrom(MemorySegment src) {
        segment.copyFrom(src);
    }

    @Override
    public long mismatch(MemorySegment other) {
        return segment.mismatch(other);
    }

    @Override
    public ByteBuffer asByteBuffer() {
        return segment.asByteBuffer();
    }

    @Override
    public byte[] toByteArray() {
        return segment.toByteArray();
    }

    @Override
    public MemorySegment asSlice(long offset) {
        return segment.asSlice(offset);
    }

    @Override
    public short[] toShortArray() {
        return segment.toShortArray();
    }

    @Override
    public char[] toCharArray() {
        return segment.toCharArray();
    }

    @Override
    public int[] toIntArray() {
        return segment.toIntArray();
    }

    @Override
    public float[] toFloatArray() {
        return segment.toFloatArray();
    }

    @Override
    public long[] toLongArray() {
        return segment.toLongArray();
    }

    @Override
    public double[] toDoubleArray() {
        return segment.toDoubleArray();
    }

    protected final MemorySegment segment() {
        return segment;
    }
}
