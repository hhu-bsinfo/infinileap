package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;

public class RemoteKey {

    private final MemoryAddress address;

    private RemoteKey(MemoryAddress address) {
        this.address = address;
    }

    MemoryAddress address() {
        return address;
    }

    static RemoteKey of(MemoryAddress address) {
        return new RemoteKey(address);
    }
}
