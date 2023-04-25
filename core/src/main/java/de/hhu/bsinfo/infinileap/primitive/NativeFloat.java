package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import java.lang.foreign.*;

public final class NativeFloat extends NativePrimitive {

    private static final int SIZE = Float.BYTES;

    public NativeFloat() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public NativeFloat(SegmentAllocator allocator) {
        this(0.0F, allocator);
    }

    public NativeFloat(float initialValue, SegmentAllocator allocator) {
        super(allocator.allocate(SIZE), DataType.CONTIGUOUS_32_BIT);
        set(initialValue);

    }

    private NativeFloat(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_32_BIT);
    }

    public void set(float value) {
        segment().set(ValueLayout.JAVA_FLOAT, 0L, value);
    }

    public float get() {
        return segment().get(ValueLayout.JAVA_FLOAT, 0L);
    }

    public static NativeFloat map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeFloat map(MemorySegment segment, long offset) {
        return new NativeFloat(segment.asSlice(offset, SIZE));
    }
}
