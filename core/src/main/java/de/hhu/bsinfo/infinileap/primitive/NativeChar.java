package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.ValueLayout;

public final class NativeChar extends NativePrimitive {

    private static final int SIZE = Character.BYTES;

    public NativeChar() {
        this(ResourceScope.newImplicitScope());
    }

    public NativeChar(ResourceScope scope) {
        this((char) 0, scope);
    }

    public NativeChar(char initialValue, ResourceScope scope) {
        super(MemorySegment.allocateNative(SIZE, scope), DataType.CONTIGUOUS_16_BIT);
        set(initialValue);
    }

    private NativeChar(MemorySegment segment) {
        super(segment, DataType.CONTIGUOUS_16_BIT);
    }

    public void set(char value) {
        segment().set(ValueLayout.JAVA_CHAR, 0L, value);
    }

    public char get() {
        return segment().get(ValueLayout.JAVA_CHAR, 0L);
    }

    public static NativeChar map(MemorySegment segment) {
        return map(segment, 0L);
    }

    public static NativeChar map(MemorySegment segment, long offset) {
        return new NativeChar(segment.asSlice(offset, SIZE));
    }
}
