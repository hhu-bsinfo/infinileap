package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.linux.rdma.ibverbs_h;

import static org.linux.rdma.ibverbs_h.ibv_context;

public final class Context extends Struct {

    public Context() {
        super(ibv_context.allocate());
    }

    public Context(MemoryAddress address) {
        super(address, ibv_context.$LAYOUT());
    }
}
