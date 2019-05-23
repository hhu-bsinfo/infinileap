package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import de.hhu.bsinfo.neutrino.util.StructUtil;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked"})
public class NativeArray<T extends NativeObject> implements NativeObject {

    private final ByteBuffer byteBuffer;
    private final ReferenceFactory<T> factory;
    private final long handle;
    private final int elementSize;
    private final int length;
    private final T[] elements;

    public NativeArray(ReferenceFactory<T> factory, Class<? extends Struct> type, final long handle, final int length) {
        this.factory = factory;
        this.length = length;
        this.handle = handle;
        elementSize = StructUtil.getSize(type);
        byteBuffer = null;
        elements = (T[]) Array.newInstance(type, length);
    }

    public NativeArray(ReferenceFactory<T> factory, final Class<? extends Struct> type, final int length) {
        this.factory = factory;
        this.length = length;
        elementSize = StructUtil.getSize(type);
        byteBuffer = ByteBuffer.allocateDirect(length * elementSize);
        handle = MemoryUtil.getAddress(byteBuffer);
        elements = (T[]) Array.newInstance(type, length);
    }

    public T get(final int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.format("Index %d is outside array with size %d", index, length));
        }

        ensureObject(index);

        return elements[index];
    }

    public void apply(final int index, final Consumer<T> operations) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.format("Index %d is outside array with size %d", index, length));
        }

        ensureObject(index);

        operations.accept(elements[index]);
    }

    public void forEach(final Consumer<T> operation) {
        for (int i = 0; i < length; i++) {
            ensureObject(i);
            operation.accept(elements[i]);
        }
    }

    public int getLength() {
        return length;
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
    public String toString() {
        return "NativeArray {" +
            "\n\telements=" + Arrays.toString(elements) +
            "\n}";
    }
}
