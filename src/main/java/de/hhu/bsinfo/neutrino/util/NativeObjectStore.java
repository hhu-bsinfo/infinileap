package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;

public interface NativeObjectStore<T extends NativeObject> {
    void storeInstance(T instance);
    T getInstance();
}
