package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ValueLayout;

public class MemoryHandle extends NativeObject {

    MemoryHandle(MemoryAddress address) {
        super(address, ValueLayout.ADDRESS);
    }
}
