package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;

import java.lang.foreign.MemorySegment;

public final class WorkerAddress extends NativeObject {

    WorkerAddress(MemorySegment base, long byteSize) {
        super(base, byteSize);
    }
}