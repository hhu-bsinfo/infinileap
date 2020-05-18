package de.hhu.bsinfo.neutrino.util.factory;

import de.hhu.bsinfo.neutrino.struct.field.NativeObject;

@FunctionalInterface
public interface ReferenceFactory<T extends NativeObject> {
    T newInstance(long handle);
}
