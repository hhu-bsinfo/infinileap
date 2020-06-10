package de.hhu.bsinfo.neutrino.verbs.panama;

import jdk.incubator.foreign.MemoryAddress;

public interface NativeObject extends AutoCloseable {

    /**
     * Returns the native object's virtual memory address.
     */
    MemoryAddress memoryAddress();

    /**
     * Returns the native object's size in bytes.
     */
    long sizeOf();
}
