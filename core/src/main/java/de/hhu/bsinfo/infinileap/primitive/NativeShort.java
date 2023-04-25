package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

public final class NativeShort extends NativePrimitive {

    private static final int SIZE = Short.BYTES;

    public NativeShort() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public NativeShort(SegmentAllocator allocator) {
        this((short) 0, allocator);
    }

    public NativeShort(short initialValue, SegmentAllocator allocator) {
        super(allocator.allocate(SIZE), DataType.CONTIGUOUS_16_BIT);
        set(initialValue);
    }

    private NativeShort(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_16_BIT);
    }

    public void set(short value) {
        segment().set(ValueLayout.JAVA_SHORT, 0L, value);
    }

    public short get() {
        return segment().get(ValueLayout.JAVA_SHORT, 0L);
    }

    public static NativeShort map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeShort map(MemorySegment segment, long offset) {
        return new NativeShort(segment.asSlice(offset, SIZE));
    }
}
