package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;

public class Listener extends NativeObject {

    /* package-private */ Listener(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }
}
