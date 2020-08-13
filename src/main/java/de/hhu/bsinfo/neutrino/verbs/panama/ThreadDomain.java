package de.hhu.bsinfo.neutrino.verbs.panama;

import static org.linux.rdma.ibverbs_h.*;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;

public final class ThreadDomain extends Struct {

    public ThreadDomain() {
        super(ibv_td.allocate());
    }

    public ThreadDomain(MemoryAddress address) {
        super(address, ibv_td.$LAYOUT());
    }

    public MemoryAddress getContext() {
        return ibv_td.context$get(segment());
    }

    public void setContext(final MemoryAddress value) {
        ibv_td.context$set(segment(), value);
    }
}
