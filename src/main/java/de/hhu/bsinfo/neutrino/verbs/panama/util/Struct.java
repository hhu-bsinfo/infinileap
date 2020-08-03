package de.hhu.bsinfo.neutrino.verbs.panama.util;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public class Struct implements MemorySegment {

    /**
     * This struct's backing memory segment.
     */
    private final MemorySegment segment;

    /**
     * This struct's base address within its segment.
     */
    private final MemoryAddress baseAddress;

    protected Struct(Supplier<MemorySegment> segmentSupplier) {
        segment = segmentSupplier.get();
        baseAddress = segment.baseAddress();
    }

    protected Struct(MemoryLayout layout, MemoryAddress address) {
        if (address.equals(MemoryAddress.NULL)) {
            throw new IllegalArgumentException("memory address is pointing at null");
        }

        segment = MemorySegment.ofNativeRestricted(address, layout.byteSize(), Thread.currentThread(), null, null);
        baseAddress = segment.baseAddress();
    }

    @Override
    public void close() {
        segment.close();
    }

    @Override
    public MemoryAddress baseAddress() {
        return segment.baseAddress();
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
}
