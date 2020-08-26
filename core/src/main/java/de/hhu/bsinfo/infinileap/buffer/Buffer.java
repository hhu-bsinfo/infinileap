package de.hhu.bsinfo.infinileap.buffer;

import de.hhu.bsinfo.infinileap.verbs.MemoryRegion;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.nio.ByteBuffer;

public class Buffer implements MemorySegment {

    private final MemorySegment base;

    private final MemoryRegion region;

    public Buffer(MemorySegment base, MemoryRegion region) {
        this.base = base;
        this.region = region;
    }

    public int localKey() {
        return region.getLocalKey();
    }

    public int remoteKey() {
        return region.getRemoteKey();
    }

    @Override
    public MemoryAddress address() {
        return base.address();
    }

    @Override
    public Thread ownerThread() {
        return base.ownerThread();
    }

    @Override
    public MemorySegment withOwnerThread(Thread newOwner) {
        return base.withOwnerThread(newOwner);
    }

    @Override
    public long byteSize() {
        return base.byteSize();
    }

    @Override
    public MemorySegment withAccessModes(int accessModes) {
        return base.withAccessModes(accessModes);
    }

    @Override
    public boolean hasAccessModes(int accessModes) {
        return base.hasAccessModes(accessModes);
    }

    @Override
    public int accessModes() {
        return base.accessModes();
    }

    @Override
    public MemorySegment asSlice(long offset, long newSize) {
        return base.asSlice(offset, newSize);
    }

    @Override
    public MemorySegment asSlice(long offset) {
        return base.asSlice(offset);
    }

    @Override
    public MemorySegment asSlice(MemoryAddress address) {
        return base.asSlice(address);
    }

    @Override
    public boolean isAlive() {
        return base.isAlive();
    }

    @Override
    public void close() {
        base.close();
    }

    @Override
    public MemorySegment fill(byte value) {
        return base.fill(value);
    }

    @Override
    public void copyFrom(MemorySegment src) {
        base.copyFrom(src);
    }

    @Override
    public long mismatch(MemorySegment other) {
        return base.mismatch(other);
    }

    @Override
    public ByteBuffer asByteBuffer() {
        return base.asByteBuffer();
    }

    @Override
    public byte[] toByteArray() {
        return base.toByteArray();
    }

    @Override
    public short[] toShortArray() {
        return base.toShortArray();
    }

    @Override
    public char[] toCharArray() {
        return base.toCharArray();
    }

    @Override
    public int[] toIntArray() {
        return base.toIntArray();
    }

    @Override
    public float[] toFloatArray() {
        return base.toFloatArray();
    }

    @Override
    public long[] toLongArray() {
        return base.toLongArray();
    }

    @Override
    public double[] toDoubleArray() {
        return base.toDoubleArray();
    }
}
