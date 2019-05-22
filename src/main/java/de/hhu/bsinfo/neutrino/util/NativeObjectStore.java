package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;

public abstract class NativeObjectStore<T extends NativeObject> {
    public abstract void storeInstance(T instance);
    protected abstract T getInstance();
}
