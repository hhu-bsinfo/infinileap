package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;
import io.netty.buffer.ByteBuf;

public class RegisteredByteBuf {

    private final ByteBuf actual;
    private final MemoryRegion memoryRegion;

    public RegisteredByteBuf(ByteBuf actual, MemoryRegion memoryRegion) {
        this.actual = actual;
        this.memoryRegion = memoryRegion;
    }

    public ByteBuf getBuffer() {
        return actual;
    }

    public int getLocalKey() {
        return memoryRegion.getLocalKey();
    }

    public int getRemoteKey() {
        return memoryRegion.getRemoteKey();
    }

    public void clear() {
        actual.clear();
    }
}
