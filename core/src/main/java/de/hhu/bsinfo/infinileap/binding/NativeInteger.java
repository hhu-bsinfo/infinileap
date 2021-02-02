package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;

public final class NativeInteger extends NativePrimitive {

    public NativeInteger() {
        this(0);
    }

    public NativeInteger(int initialValue) {
        super(MemorySegment.allocateNative(CLinker.C_INT), DataType.CONTIGUOUS_32_BIT);
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
        return new NativeInteger(segment.asSlice(offset, CLinker.C_INT.byteSize()));
    }
}
