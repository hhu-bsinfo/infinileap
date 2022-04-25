package de.hhu.bsinfo.infinileap.util.factory;

import de.hhu.bsinfo.infinileap.binding.NativeObject;
import jdk.incubator.foreign.MemoryAddress;

@FunctionalInterface
public interface ReferenceFactory<T extends NativeObject> {
    T newInstance(MemoryAddress address);
}
