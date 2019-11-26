package de.hhu.bsinfo.neutrino.util;

import java.util.Objects;
import java.util.function.Supplier;

public class RingBufferPool<T extends Poolable> extends Pool<T> {

    private final RingBuffer<T> buffer;

    public RingBufferPool(final int size, final Supplier<T> supplier) {
        super(supplier);

        buffer = new RingBuffer<>(size);

        for(int i = 0; i < size; i++) {
            buffer.push(supplier.get());
        }
    }

    @Override
    public final T getInstance() {
        return Objects.requireNonNullElseGet(buffer.pop(), getSupplier());
    }

    @Override
    public void returnInstance(T instance) {
        buffer.push(instance);
    }
}
