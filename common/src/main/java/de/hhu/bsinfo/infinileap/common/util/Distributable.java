package de.hhu.bsinfo.infinileap.common.util;

import jdk.incubator.foreign.MemorySegment;

public interface Distributable {

    /**
     * Serializes this object into the specified memory segment.
     * @param target The target memory segment.
     * @return The number of bytes written.
     */
    long writeTo(MemorySegment target);

    /**
     * Deserializes the specified memory segment into this object.
     * @param source The source memory segment.
     */
    void readFrom(MemorySegment source);
}
