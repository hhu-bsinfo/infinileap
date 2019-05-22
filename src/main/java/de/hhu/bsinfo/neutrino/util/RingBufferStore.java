package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;

public class RingBufferStore<T extends NativeObject> extends NativeObjectStore<T> {

    private final RingBuffer<T> buffer;

    public RingBufferStore(final int size) {
        buffer = new RingBuffer<>(size);
    }

    @Override
    public final void storeInstance(final T instance) {
        buffer.push(instance);
    }

    @Override
    public T getInstance() {
        return buffer.pop();
    }
}
