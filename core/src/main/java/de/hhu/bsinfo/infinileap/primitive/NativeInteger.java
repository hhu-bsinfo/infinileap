package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;

public final class NativeInteger extends NativePrimitive {

    private static final int SIZE = Integer.BYTES;

    public NativeInteger() {
        this(0);
    }

    public NativeInteger(int initialValue) {
        super(MemorySegment.allocateNative(SIZE), DataType.CONTIGUOUS_32_BIT);
        set(initialValue);
    }

    private NativeInteger(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_32_BIT);
    }

    public void set(int value) {
        MemoryAccess.setInt(segment(), value);
    }

    public int get() {
        return MemoryAccess.getInt(segment());
    }

    public static NativeInteger map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeInteger map(MemorySegment segment, long offset) {
        return new NativeInteger(segment.asSlice(offset, SIZE));
    }
}
