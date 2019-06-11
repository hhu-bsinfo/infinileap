package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.verbs.AccessFlag;
import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;
import de.hhu.bsinfo.neutrino.verbs.MemoryWindow;
import de.hhu.bsinfo.neutrino.verbs.MemoryWindow.BindAttributes;
import de.hhu.bsinfo.neutrino.verbs.MemoryWindow.Type;
import de.hhu.bsinfo.neutrino.verbs.QueuePair;
import de.hhu.bsinfo.neutrino.verbs.ScatterGatherElement;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest.SendFlag;
import org.jetbrains.annotations.Nullable;

public class RegisteredBuffer extends LocalBuffer implements AutoCloseable {

    private final MemoryRegion memoryRegion;

    /**
     * Wraps a normal memory region, which is present in the host's memory
     */
    public RegisteredBuffer(MemoryRegion memoryRegion) {
        this(memoryRegion, null);
    }

    /**
     * Wraps a normal memory region, which is present in the host's memory
     */
    public RegisteredBuffer(MemoryRegion memoryRegion, Object parent) {
        super(memoryRegion.getAddress(), memoryRegion.getLength(), parent);
        this.memoryRegion = memoryRegion;
    }

    /**
     * Wraps a memory region, which is not present in the host's memory,
     * and needs a separate buffer in the host's memory to synchronize with (e.g. device memory).
     */
    public RegisteredBuffer(MemoryRegion memoryRegion, long handle, long capacity) {
        this(memoryRegion, handle, capacity, null);
    }

    /**
     * Wraps a memory region, which is not present in the host's memory,
     * and needs a separate buffer in the host's memory to synchronize with (e.g. device memory).
     */
    public RegisteredBuffer(MemoryRegion memoryRegion, long handle, long capacity, Object parent) {
        super(handle, capacity, parent);
        this.memoryRegion = memoryRegion;
    }

    MemoryRegion getMemoryRegion() {
        return memoryRegion;
    }

    public int getLocalKey() {
        return memoryRegion.getLocalKey();
    }

    public int getRemoteKey() {
        return memoryRegion.getRemoteKey();
    }

    public long read(RemoteBuffer remoteBuffer) {
        return read(0, remoteBuffer, 0, remoteBuffer.capacity());
    }

    public long read(long index, RemoteBuffer remoteBuffer, long offset, long length) {
        return remoteBuffer.read(index, this, offset, length);
    }

    public long write(RemoteBuffer remoteBuffer) {
        return write(0, remoteBuffer, 0, remoteBuffer.capacity());
    }

    public long write(long index, RemoteBuffer remoteBuffer, long offset, long length) {
        return remoteBuffer.write(index, this, offset, length);
    }

    public ScatterGatherElement.Array split() {
        return split(0, capacity());
    }

    public ScatterGatherElement.Array split(final long offset, final long length) {
        if (offset < 0 || offset >= capacity() || offset + length <= 0 || offset + length >= capacity()) {
            throw new IllegalArgumentException(String.format("invalid offset %d with length %d", offset, length));
        }

        int slots = (int) (length / Integer.MAX_VALUE) + 1;
        int remainder = (int) (length & Integer.MAX_VALUE);

        var array = new ScatterGatherElement.Array(slots + 1);
        return array.forEachIndexed((index, element) -> {
            element.setLocalKey(getLocalKey());
            element.setLength(index == slots ? Integer.MAX_VALUE : remainder);
            element.setAddress(getHandle() + offset + (long) index * Integer.MAX_VALUE);
        });
    }

    public RegisteredBufferWindow bindMemoryWindow(MemoryWindow memoryWindow, QueuePair queuePair, long offset, long length, AccessFlag... flags) {
        BindAttributes attributes = new BindAttributes(config -> {
            config.setSendFlags(SendFlag.SIGNALED);

            config.bindInfo.setMemoryRegion(memoryRegion);
            config.bindInfo.setAddress(getHandle() + offset);
            config.bindInfo.setLength(length);
            config.bindInfo.setAccessFlags(flags);
        });

        if(!memoryWindow.bind(queuePair, attributes)) {
            return null;
        }

        return new RegisteredBufferWindow(this, memoryWindow, offset, length);
    }

    @Nullable
    public RegisteredBufferWindow allocateAndBindMemoryWindow(QueuePair queuePair, long offset, long length, AccessFlag... flags) {
        MemoryWindow memoryWindow = queuePair.getProtectionDomain().allocateMemoryWindow(Type.TYPE_1);
        if (memoryWindow == null) {
            return null;
        }

        return bindMemoryWindow(memoryWindow, queuePair, offset, length, flags);
    }

    @Override
    public String toString() {
        return "RegisteredBuffer {" +
            "\n\t" + super.toString() +
            ",\n\tmemoryRegion=" + memoryRegion +
            "\n}";
    }

    @Override
    public void close() {
        memoryRegion.close();
    }
}
