package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

public final class NativeByte extends NativePrimitive {

    private static final int SIZE = Byte.BYTES;

    public NativeByte() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public NativeByte(SegmentAllocator allocator) {
        this((byte) 0, allocator);
    }

    public NativeByte(byte initialValue, SegmentAllocator allocator) {
        super(allocator.allocate(SIZE), DataType.CONTIGUOUS_8_BIT);
        set(initialValue);
    }

    private NativeByte(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_8_BIT);
    }

    public void set(byte value) {
        segment().set(ValueLayout.JAVA_BYTE, 0L, value);
    }

    public byte get() {
        return segment().get(ValueLayout.JAVA_BYTE, 0L);
    }

    public static NativeByte map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeByte map(MemorySegment segment, long offset) {
        return new NativeByte(segment.asSlice(offset, SIZE));
    }
}
