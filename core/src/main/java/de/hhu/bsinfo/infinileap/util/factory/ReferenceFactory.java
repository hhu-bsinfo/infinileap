package de.hhu.bsinfo.infinileap.util.factory;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

@FunctionalInterface
public interface ReferenceFactory<T extends MemorySegment> {
    T newInstance(MemoryAddress address);
}
