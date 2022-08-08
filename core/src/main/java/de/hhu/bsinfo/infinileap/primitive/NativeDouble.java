package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;

public final class NativeDouble extends NativePrimitive {

    private static final int SIZE = Double.BYTES;

    public NativeDouble() {
        this(MemorySession.openImplicit());
    }

    public NativeDouble(MemorySession session) {
        this(0.0, session);
    }

    public NativeDouble(double initialValue, MemorySession session) {
        super(MemorySegment.allocateNative(SIZE, session), DataType.CONTIGUOUS_64_BIT);
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
