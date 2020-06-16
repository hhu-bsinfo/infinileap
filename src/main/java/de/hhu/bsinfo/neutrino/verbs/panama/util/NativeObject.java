package de.hhu.bsinfo.neutrino.verbs.panama.util;

import jdk.incubator.foreign.MemoryAddress;

import java.io.Closeable;

public interface NativeObject extends Closeable {

    /**
     * Returns the native object's virtual memory address.
     */
    MemoryAddress memoryAddress();

    /**
     * Returns the native object's size in bytes.
     */
    long sizeOf();
}
