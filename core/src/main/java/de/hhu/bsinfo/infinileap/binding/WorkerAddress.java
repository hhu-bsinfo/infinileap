package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;

public final class WorkerAddress extends NativeObject {

    WorkerAddress(MemoryAddress address, long byteSize) {
        super(address, byteSize);
    }
}