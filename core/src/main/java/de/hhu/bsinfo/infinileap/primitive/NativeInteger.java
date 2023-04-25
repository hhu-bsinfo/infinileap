package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

public final class NativeInteger extends NativePrimitive {

    private static final int SIZE = Integer.BYTES;

    public NativeInteger() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public NativeInteger(int initialValue) {
        this(initialValue, SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public NativeInteger(SegmentAllocator allocator) {
        this(0, allocator);
    }

    public NativeInteger(int initialValue, SegmentAllocator allocator) {
        super(allocator.allocate(SIZE), DataType.CONTIGUOUS_32_BIT);
        set(initialValue);
    }

    private NativeInteger(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_32_BIT);
    }

    public void set(int value) {
        segment().set(ValueLayout.JAVA_INT, 0L, value);
    }

    public int get() {
        return segment().get(ValueLayout.JAVA_INT, 0L);
    }

    public static NativeInteger map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeInteger map(MemorySegment segment, long offset) {
        return new NativeInteger(segment.asSlice(offset, SIZE));
    }
}
