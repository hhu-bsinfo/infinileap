package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.util.factory.ReferenceFactory;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;

import java.lang.reflect.Array;

public class NativeArray<T extends MemorySegment> extends NativeObject {

    /**
     * This array's capacity expressed in number of elements.
     */
    private int capacity;

    /**
     * Factory used to wrap the elements within this array.
     */
    private final ReferenceFactory<T> factory;

    /**
     * The element's memory layout.
     */
    private final MemoryLayout elementLayout;

    /**
     * Array of preallocated objects pointing at the elements contained within this array.
     */
    private final T[] elements;

    private NativeArray(MemorySegment segment, ReferenceFactory<T> factory, Class<T> type, MemoryLayout layout, int capacity) {
        super(segment);

        elements = (T[]) Array.newInstance(type, capacity);
        this.factory = factory;
        this.elementLayout = layout;
        this.capacity = capacity;
    }

    public static <T extends MemorySegment> NativeArray<T> allocate(ReferenceFactory<T> factory, Class<T> type,  MemoryLayout layout, int capacity) {
        var segment = MemorySegment.allocateNative(layout.byteSize() * capacity);
        return new NativeArray<>(segment, factory, type, layout, capacity);
    }
}
