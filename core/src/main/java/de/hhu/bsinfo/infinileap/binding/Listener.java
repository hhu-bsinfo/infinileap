package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;

public class Listener extends NativeObject {

    /* package-private */ Listener(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }
}
