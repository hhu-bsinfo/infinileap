package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public final class NativeLong extends NativePrimitive {

    private static final int SIZE = Long.BYTES;

    public NativeLong() {
        this(ResourceScope.newImplicitScope());
    }

    public NativeLong(ResourceScope scope) {
        this(0, scope);
    }

    public NativeLong(long initialValue, ResourceScope scope) {
        super(MemorySegment.allocateNative(SIZE, scope), DataType.CONTIGUOUS_64_BIT);
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
