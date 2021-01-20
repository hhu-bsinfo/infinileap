package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public class MemoryHandle extends NativeObject {

    MemoryHandle(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }
}
