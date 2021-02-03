package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;

public final class NativeLong extends NativePrimitive {

    private static final int SIZE = Long.BYTES;

    public NativeLong() {
        this(0);
    }

    public NativeLong(long initialValue) {
        super(MemorySegment.allocateNative(SIZE), DataType.CONTIGUOUS_64_BIT);
        set(initialValue);
    }

    private NativeLong(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_64_BIT);
    }

    public void set(long value) {
        MemoryAccess.setLong(segment(), value);
    }

    public long get() {
        return MemoryAccess.getLong(segment());
    }

    public static NativeLong map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeLong map(MemorySegment segment, long offset) {
        return new NativeLong(segment.asSlice(offset, SIZE));
    }
}
