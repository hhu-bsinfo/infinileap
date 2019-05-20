package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class NativeArray<T extends NativeObject> implements NativeObject {

    private final ByteBuffer byteBuffer;
    private final ReferenceFactory<T> factory;
    private final long handle;
    private final int elementSize;
    private final int length;

    public NativeArray(final long handle, final int elementSize, final int length, ReferenceFactory<T> factory) {
        this.factory = factory;
        this.elementSize = elementSize;
        this.length = length;
        this.handle = handle;
        byteBuffer = null;
    }

    public NativeArray(final int elementSize, final int length, ReferenceFactory<T> factory) {
        this.factory = factory;
        this.elementSize = elementSize;
        this.length = length;
        byteBuffer = ByteBuffer.allocateDirect(length * elementSize);
        handle = MemoryUtil.getAddress(byteBuffer);
    }

    public T get(final int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.format("Index %d is outside array with size %d", index, length));
        }

        return factory.newInstance(handle + (long) index * elementSize);
    }

    public void apply(final int index, final Consumer<T> operations) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.format("Index %d is outside array with size %d", index, length));
        }

        operations.accept(get(index));
    }

    @Override
    public long getHandle() {
        return handle;
    }
}
