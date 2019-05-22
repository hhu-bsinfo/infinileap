package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.verbs.Verbs;

public interface NativeObject {
    long getHandle();

    default void free() {
        Verbs.getNativeObjectPool(getClass()).storeInstance(this);
    }
}
