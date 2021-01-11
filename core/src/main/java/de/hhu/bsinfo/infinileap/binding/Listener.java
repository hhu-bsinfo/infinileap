package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import static org.openucx.ucx_h.*;

public class Listener extends NativeObject {

    /* package-private */ Listener(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }
}
