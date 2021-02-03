package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;

public final class NativeByte extends NativePrimitive {

    private static final int SIZE = Byte.BYTES;

    public NativeByte() {
        this((byte) 0);
    }

    public NativeByte(byte initialValue) {
        super(MemorySegment.allocateNative(SIZE), DataType.CONTIGUOUS_8_BIT);
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
