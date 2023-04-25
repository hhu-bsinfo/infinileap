package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

public final class NativeLong extends NativePrimitive {

    private static final int SIZE = Long.BYTES;

    public NativeLong() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public NativeLong(SegmentAllocator allocator) {
        this(0, allocator);
    }

    public NativeLong(long initialValue, SegmentAllocator allocator) {
        super(allocator.allocate(SIZE), DataType.CONTIGUOUS_64_BIT);
        set(initialValue);
    }

    private NativeLong(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_64_BIT);
    }

    public void set(long value) {
        segment().set(ValueLayout.JAVA_LONG, 0L, value);
    }

    public long get() {
        return segment().get(ValueLayout.JAVA_LONG, 0L);
    }

    public static NativeLong map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeLong map(MemorySegment segment, long offset) {
        return new NativeLong(segment.asSlice(offset, SIZE));
    }
}
