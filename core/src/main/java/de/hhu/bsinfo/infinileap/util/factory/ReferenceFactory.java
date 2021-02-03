package de.hhu.bsinfo.infinileap.util.factory;

import de.hhu.bsinfo.infinileap.binding.NativeObject;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

@FunctionalInterface
public interface ReferenceFactory<T extends NativeObject> {
    T newInstance(MemoryAddress address);
}
