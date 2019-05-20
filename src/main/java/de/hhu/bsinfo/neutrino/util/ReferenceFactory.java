package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;

public interface ReferenceFactory<T extends NativeObject> {
    T newInstance(long handle);
}
