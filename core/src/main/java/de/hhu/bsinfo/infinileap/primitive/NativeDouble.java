package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;

public final class NativeDouble extends NativePrimitive {

    private static final int SIZE = Double.BYTES;

    public NativeDouble() {
        this(0.0);
    }

    public NativeDouble(double initialValue) {
        super(MemorySegment.allocateNative(SIZE), DataType.CONTIGUOUS_64_BIT);
        set(initialValue);
    }

    private NativeDouble(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_64_BIT);
    }

    public void set(double value) {
        MemoryAccess.setDouble(segment(), value);
    }

    public double get() {
        return MemoryAccess.getDouble(segment());
    }

    public static NativeDouble map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeDouble map(MemorySegment segment, long offset) {
        return new NativeDouble(segment.asSlice(offset, SIZE));
    }
}
