package de.hhu.bsinfo.infinileap.verbs;

import static org.linux.rdma.infinileap_h.*;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;

public final class ThreadDomain extends NativeObject {

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
