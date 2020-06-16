package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;

import static org.linux.rdma.ibverbs_h.Cibv_context;

public final class Context extends Struct {

    public Context() {
        super(Cibv_context::allocate);
    }

    public Context(MemoryAddress address) {
        super(Cibv_context.$LAYOUT(), address);
    }
}
