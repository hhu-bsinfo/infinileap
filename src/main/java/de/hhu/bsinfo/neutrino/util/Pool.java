package de.hhu.bsinfo.neutrino.util;

import java.util.function.Supplier;

public abstract class Pool<T extends Poolable> {

    private final Supplier<T> supplier;

    protected Pool(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public abstract T getInstance();
    public abstract void returnInstance(T instance);

    public Supplier<T> getSupplier() {
        return supplier;
    }
}
