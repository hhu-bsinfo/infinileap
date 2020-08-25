package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.Addressable;
import jdk.incubator.foreign.CSupport;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public class MemoryUtil {

    public static MemorySegment allocateMemory(long capacity) {
        return MemorySegment.ofNativeRestricted(
                CSupport.allocateMemoryRestricted(capacity),
                capacity,
                null,
                null,
                null
        );
    }

    public static void freeMemory(Addressable addressable) {
        CSupport.freeMemoryRestricted(addressable.address());
    }
}
