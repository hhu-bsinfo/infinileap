package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;

public class RemoteMemorySegment {

    private final Endpoint owner;

    private final MemoryAddress address;

    private final RemoteKey key;

    private final long size;

    RemoteMemorySegment(Endpoint owner, MemoryAddress address, RemoteKey key, long size) {
        this.owner = owner;
        this.address = address;
        this.key = key;
        this.size = size;
    }

    public long byteSize() {
        return size;
    }

    public Endpoint owner() {
        return owner;
    }
}
