package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;

@FunctionalInterface
public interface NativeObjectFactory<T extends NativeObject> {
    T newInstance();
}
