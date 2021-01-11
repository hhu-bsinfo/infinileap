package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;

public class ConnectionRequest {

    private final MemoryAddress address;

    private final long data;

    private ConnectionRequest(MemoryAddress address, long data) {
        this.address = address;
        this.data = data;
    }

    MemoryAddress address() {
        return address;
    }

    public long getData() {
        return data;
    }

    static ConnectionRequest of(MemoryAddress address, long data) {
        return new ConnectionRequest(address, data);
    }
}
