package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;

public final class NativeShort extends NativePrimitive {

    private static final int SIZE = Short.BYTES;

    public NativeShort() {
        this((short) 0);
    }

    public NativeShort(short initialValue) {
        super(MemorySegment.allocateNative(SIZE), DataType.CONTIGUOUS_16_BIT);
        set(initialValue);
    }

    private NativeShort(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_16_BIT);
    }

    public void set(short value) {
        MemoryAccess.setShort(segment(), value);
    }

    public short get() {
        return MemoryAccess.getShort(segment());
    }

    public static NativeShort map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeShort map(MemorySegment segment, long offset) {
        return new NativeShort(segment.asSlice(offset, SIZE));
    }
}
