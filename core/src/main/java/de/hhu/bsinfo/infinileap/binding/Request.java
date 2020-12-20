package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;

public class Request {

    private final MemoryAddress address;

    private Request(MemoryAddress address) {
        this.address = address;
    }

    MemoryAddress address() {
        return address;
    }

    static Request of(MemoryAddress address) {
        return new Request(address);
    }
}
