package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.ValueLayout;

public class MemoryHandle extends NativeObject {

    MemoryHandle(MemoryAddress address) {
        super(address, ValueLayout.ADDRESS);
    }
}
