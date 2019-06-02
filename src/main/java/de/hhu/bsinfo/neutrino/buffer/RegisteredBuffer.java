package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;

public class RegisteredBuffer extends LocalBuffer implements AutoCloseable {

    private final MemoryRegion memoryRegion;

    public RegisteredBuffer(MemoryRegion memoryRegion) {
        this(memoryRegion, null);
    }

    public RegisteredBuffer(MemoryRegion memoryRegion, Object parent) {
        super(memoryRegion.getAddress(), memoryRegion.getLength(), parent);
        this.memoryRegion = memoryRegion;
    }

    public int getLocalKey() {
        return memoryRegion.getLocalKey();
    }

    public int getRemoteKey() {
        return memoryRegion.getRemoteKey();
    }

    public void read(RemoteBuffer remoteBuffer) {
        remoteBuffer.read(this);
    }

    public void write(RemoteBuffer remoteBuffer) {
        remoteBuffer.write(this);
    }

    @Override
    public String toString() {
        return "RegisteredBuffer {" +
            "\n\tmemoryRegion=" + memoryRegion +
            "\n}";
    }

    @Override
    public void close() {
        memoryRegion.deregister();
    }
}
