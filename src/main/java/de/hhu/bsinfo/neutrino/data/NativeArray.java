package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import de.hhu.bsinfo.neutrino.util.StructUtil;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked"})
public class NativeArray<T extends NativeObject> implements NativeObject {

    private final ByteBuffer byteBuffer;
    private final ReferenceFactory<T> factory;
    private final long handle;
    private final int elementSize;
    private int capacity;
    private final T[] elements;

    public NativeArray(ReferenceFactory<T> factory, Class<? extends Struct> type, final long handle, final int capacity) {
        this.factory = factory;
        this.capacity = capacity;
        this.handle = handle;
        elementSize = StructUtil.getSize(type);
        byteBuffer = null;
        elements = (T[]) Array.newInstance(type, capacity);
    }

    public NativeArray(ReferenceFactory<T> factory, final Class<? extends Struct> type, final int capacity) {
        this.factory = factory;
        this.capacity = capacity;
        elementSize = StructUtil.getSize(type);
        byteBuffer = ByteBuffer.allocateDirect(capacity * elementSize);
        handle = MemoryUtil.getAddress(byteBuffer);
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

    public void apply(final int index, final Consumer<T> operations) {
        if (index >= capacity) {
            throw new IndexOutOfBoundsException(String.format("Index %d is outside array with size %d", index,
                capacity));
        }

        ensureObject(index);

        operations.accept(elements[index]);
    }

    public void forEach(final Consumer<T> operation) {
        for (int i = 0; i < capacity; i++) {
            ensureObject(i);
            operation.accept(elements[i]);
        }
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
    public long getNativeSize() {
        return (long) elementSize * capacity;
    }

    @Override
    public String toString() {
        return "NativeArray {" +
            "\n\telements=" + Arrays.toString(elements) +
            "\n}";
    }
}
