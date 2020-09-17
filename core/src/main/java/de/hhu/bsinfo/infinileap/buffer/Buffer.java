package de.hhu.bsinfo.infinileap.buffer;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.verbs.MemoryRegion;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.nio.ByteBuffer;

public class Buffer extends NativeObject {

    private final MemoryRegion region;

    public Buffer(MemorySegment base, MemoryRegion region) {
        super(base);
        this.region = region;
    }

    public int localKey() {
        return region.getLocalKey();
    }

    public int remoteKey() {
        return region.getRemoteKey();
    }
}
