package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public final class NativeInteger extends NativePrimitive {

    private static final int SIZE = Integer.BYTES;

    public NativeInteger() {
        this(ResourceScope.newImplicitScope());
    }

    public NativeInteger(int initialValue) {
        this(initialValue, ResourceScope.newImplicitScope());
    }

    public NativeInteger(ResourceScope scope) {
        this(0, scope);
    }

    public NativeInteger(int initialValue, ResourceScope scope) {
        super(MemorySegment.allocateNative(SIZE, scope), DataType.CONTIGUOUS_32_BIT);
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
