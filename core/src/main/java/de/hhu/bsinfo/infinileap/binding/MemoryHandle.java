package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ValueLayout;

public class MemoryHandle extends NativeObject {

    MemoryHandle(MemoryAddress address) {
        super(address, ValueLayout.ADDRESS);
    }
}
