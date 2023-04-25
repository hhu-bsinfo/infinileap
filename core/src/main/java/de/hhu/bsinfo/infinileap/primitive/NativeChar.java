package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

public final class NativeChar extends NativePrimitive {

    private static final int SIZE = Character.BYTES;

    public NativeChar() {
        this(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
    }

    public NativeChar(SegmentAllocator allocator) {
        this((char) 0, allocator);
    }

    public NativeChar(char initialValue, SegmentAllocator allocator) {
        super(allocator.allocate(SIZE), DataType.CONTIGUOUS_16_BIT);
        set(initialValue);
    }

    private NativeChar(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_16_BIT);
    }

    public void set(char value) {
        segment().set(ValueLayout.JAVA_CHAR, 0L, value);
    }

    public char get() {
        return segment().get(ValueLayout.JAVA_CHAR, 0L);
    }

    public static NativeChar map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeChar map(MemorySegment segment, long offset) {
        return new NativeChar(segment.asSlice(offset, SIZE));
    }
}
