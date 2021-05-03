package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public final class WorkerAddress extends NativeObject {

    WorkerAddress(MemoryAddress address, long byteSize) {
        super(address, byteSize);
    }
}