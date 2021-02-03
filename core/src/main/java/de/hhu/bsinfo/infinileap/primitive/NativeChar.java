package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;

public final class NativeChar extends NativePrimitive {

    private static final int SIZE = Character.BYTES;

    public NativeChar() {
        this((char) 0);
    }

    public NativeChar(char initialValue) {
        super(MemorySegment.allocateNative(SIZE), DataType.CONTIGUOUS_16_BIT);
        set(initialValue);
    }

    private NativeChar(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_16_BIT);
    }

    public void set(char value) {
        MemoryAccess.setChar(segment(), value);
    }

    public char get() {
        return MemoryAccess.getChar(segment());
    }

    public static NativeChar map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeChar map(MemorySegment segment, long offset) {
        return new NativeChar(segment.asSlice(offset, SIZE));
    }
}
