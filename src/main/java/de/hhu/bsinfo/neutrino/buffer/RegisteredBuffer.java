package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;
import de.hhu.bsinfo.neutrino.verbs.ScatterGatherElement;

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

    public ScatterGatherElement.Array split() {
        return split(0, capacity());
    }

    public ScatterGatherElement.Array split(final long offset, final long length) {
        long capacity = Math.abs(length - offset);
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }

        int slots = (int) (capacity / Integer.MAX_VALUE) + 1;
        int remainder = (int) (capacity & Integer.MAX_VALUE);

        var array = new ScatterGatherElement.Array(slots + 1);
        return array.forEachIndexed((index, element) -> {
            element.setLocalKey(getLocalKey());
            element.setLength(index == slots ? Integer.MAX_VALUE : remainder);
            element.setAddress(getHandle() + offset + (long) index * Integer.MAX_VALUE);
        });
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
