package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import java.util.Objects;
import java.util.function.Supplier;

public class RingBufferPool<T extends NativeObject> extends RingBufferStore<T> implements NativeObjectFactory<T> {

    private final Supplier<T> supplier;

    public RingBufferPool(final int size, final Supplier<T> supplier) {
        super(size);

        this.supplier = supplier;

        for(int i = 0; i < size; i++) {
            storeInstance(supplier.get());
        }
    }

    @Override
    public final T newInstance() {
        return Objects.requireNonNullElseGet(getInstance(), supplier);
    }
}
