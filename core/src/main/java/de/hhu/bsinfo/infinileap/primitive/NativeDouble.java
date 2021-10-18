package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.ValueLayout;

public final class NativeDouble extends NativePrimitive {

    private static final int SIZE = Double.BYTES;

    public NativeDouble() {
        this(ResourceScope.newImplicitScope());
    }

    public NativeDouble(ResourceScope scope) {
        this(0.0, scope);
    }

    public NativeDouble(double initialValue, ResourceScope scope) {
        super(MemorySegment.allocateNative(SIZE, scope), DataType.CONTIGUOUS_64_BIT);
        set(initialValue);
    }

    private NativeDouble(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_64_BIT);
    }

    public void set(double value) {
        segment().set(ValueLayout.JAVA_DOUBLE, 0L, value);
    }

    public double get() {
        return segment().get(ValueLayout.JAVA_DOUBLE, 0L);
    }

    public static NativeDouble map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeDouble map(MemorySegment segment, long offset) {
        return new NativeDouble(segment.asSlice(offset, SIZE));
    }
}
