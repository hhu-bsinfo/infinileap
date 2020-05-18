package de.hhu.bsinfo.neutrino.struct.field;

import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.*;
import de.hhu.bsinfo.neutrino.util.factory.ReferenceFactory;
import org.agrona.concurrent.AtomicBuffer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked"})
public class NativeArray<T extends NativeObject> implements NativeObject {

    /**
     * This array's underlying native buffer.
     */
    private final AtomicBuffer buffer;

    /**
     * Factory used to wrap the elements within this array.
     */
    private final ReferenceFactory<T> factory;

    /**
     * This array's virtual memory address.
     */
    private final long handle;

    /**
     * This array's capacity expressed in number of elements.
     */
    private int capacity;

    /**
     * A single element's size in bytes.
     */
    private final int elementSize;

    /**
     * Array of preallocated objects pointing at the elements contained within this array.
     */
    private final T[] elements;


    public NativeArray(ReferenceFactory<T> factory, Class<? extends Struct> type, final long handle, final int capacity) {
        this.factory = factory;
        this.capacity = capacity;
        this.handle = handle;
        elementSize = StructUtil.getSize(type);
        buffer = MemoryUtil.wrap(handle, capacity * elementSize);
        elements = (T[]) Array.newInstance(type, capacity);
    }

    public NativeArray(ReferenceFactory<T> factory, final Class<? extends Struct> type, final int capacity) {
        this.factory = factory;
        this.capacity = capacity;
        elementSize = StructUtil.getSize(type);
        buffer = MemoryUtil.allocateAligned(capacity * elementSize, MemoryAlignment.CACHE);
        handle = buffer.addressOffset();
        elements = (T[]) Array.newInstance(type, capacity);
    }

    public T get(final int index) {
        if (index >= capacity) {
            throw new IndexOutOfBoundsException(String.format("Index %d is outside array with size %d", index,
                capacity));
        }

        ensureObject(index);

        return elements[index];
    }

    protected T getUnchecked(final int index) {
        ensureObject(index);
        return elements[index];
    }

    public <S extends NativeArray<T>> S apply(final int index, final Consumer<T> operations) {
        if (index >= capacity) {
            throw new IndexOutOfBoundsException(String.format("Index %d is outside array with size %d", index,
                capacity));
        }

        ensureObject(index);
        operations.accept(elements[index]);

        return (S) this;
    }

    public <S extends NativeArray<T>> S forEach(final Consumer<T> operation) {
        return forEach(capacity, operation);
    }

    public <S extends NativeArray<T>> S forEach(int limit, final Consumer<T> operation)  {
        for (int i = 0; i < limit; i++) {
            ensureObject(i);
            operation.accept(elements[i]);
        }

        return (S) this;
    }

    public <S extends NativeArray<T>> S forEachIndexed(final IndexedConsumer<T> operation) {
        for (int i = 0; i < capacity; i++) {
            ensureObject(i);
            operation.accept(i, elements[i]);
        }

        return (S) this;
    }

    protected void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    private void ensureObject(final int index) {
        if (elements[index] == null) {
            elements[index] = factory.newInstance(handle + (long) index * elementSize);
        }
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public int getNativeSize() {
        return elementSize * capacity;
    }

    @Override
    public String toString() {
        return "NativeArray {" +
            "\n\telements=" + Arrays.toString(elements) +
            "\n}";
    }
}
