package de.hhu.bsinfo.neutrino.verbs.panama;

import static org.linux.rdma.ibverbs_h.*;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;

public final class AddressHandle extends Struct {

    public AddressHandle() {
        super(ibv_ah.allocate());
    }

    public AddressHandle(MemoryAddress address) {
        super(address, ibv_ah.$LAYOUT());
    }

    public MemoryAddress getContext() {
        return ibv_ah.context$get(segment());
    }

    public MemoryAddress getProtectionDomain() {
        return ibv_ah.pd$get(segment());
    }

    public int getHandle() {
        return ibv_ah.handle$get(segment());
    }

    public void setContext(final MemoryAddress value) {
        ibv_ah.context$set(segment(), value);
    }

    public void setProtectionDomain(final MemoryAddress value) {
        ibv_ah.pd$set(segment(), value);
    }

    public void setHandle(final int value) {
        ibv_ah.handle$set(segment(), value);
    }
}
