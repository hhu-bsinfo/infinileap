package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public final class NativeByte extends NativePrimitive {

    private static final int SIZE = Byte.BYTES;

    public NativeByte() {
        this(ResourceScope.newImplicitScope());
    }

    public NativeByte(ResourceScope scope) {
        this((byte) 0, scope);
    }

    public NativeByte(byte initialValue, ResourceScope scope) {
        super(MemorySegment.allocateNative(SIZE, scope), DataType.CONTIGUOUS_8_BIT);
        set(initialValue);
    }

    private NativeByte(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_8_BIT);
    }

    public void set(byte value) {
        MemoryAccess.setByte(segment(), value);
    }

    public byte get() {
        return MemoryAccess.getByte(segment());
    }

    public static NativeByte map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeByte map(MemorySegment segment, long offset) {
        return new NativeByte(segment.asSlice(offset, SIZE));
    }
}
