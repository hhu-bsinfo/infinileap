package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public final class NativeFloat extends NativePrimitive {

    private static final int SIZE = Float.BYTES;

    public NativeFloat() {
        this(ResourceScope.newImplicitScope());
    }

    public NativeFloat(ResourceScope scope) {
        this(0.0F, scope);
    }

    public NativeFloat(float initialValue, ResourceScope scope) {
        super(MemorySegment.allocateNative(SIZE, scope), DataType.CONTIGUOUS_32_BIT);
        set(initialValue);
    }

    private NativeFloat(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_32_BIT);
    }

    public void set(float value) {
        MemoryAccess.setFloat(segment(), value);
    }

    public float get() {
        return MemoryAccess.getFloat(segment());
    }

    public static NativeFloat map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeFloat map(MemorySegment segment, long offset) {
        return new NativeFloat(segment.asSlice(offset, SIZE));
    }
}
