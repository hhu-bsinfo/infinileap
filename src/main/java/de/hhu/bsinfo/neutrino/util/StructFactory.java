package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.struct.Struct;

public interface StructFactory<T extends Struct> {
    T newInstance(long handle);
}
