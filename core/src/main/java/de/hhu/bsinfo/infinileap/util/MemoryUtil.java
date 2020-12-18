package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.Addressable;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public class MemoryUtil {

    public static MemorySegment allocateMemory(long capacity) {
        return MemorySegment.ofNativeRestricted().asSlice(
                CLinker.allocateMemoryRestricted(capacity), capacity);
    }

    public static void freeMemory(Addressable addressable) {
        CLinker.freeMemoryRestricted(addressable.address());
    }

    public static MemorySegment createSegment(MemoryAddress address, long capacity) {
        return MemorySegment.ofNativeRestricted().asSlice(address, capacity);
    }
}
