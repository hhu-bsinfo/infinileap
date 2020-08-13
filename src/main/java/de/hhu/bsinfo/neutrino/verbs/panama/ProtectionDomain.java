package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.linux.rdma.ibverbs_h.*;

public class ProtectionDomain extends Struct {

    public ProtectionDomain() {
        super(ibv_pd.allocate());
    }

    public ProtectionDomain(MemoryAddress address) {
        super(address, ibv_pd.$LAYOUT());
    }

    public MemoryAddress getContext() {
        return ibv_pd.context$get(segment());
    }

    public int getHandle() {
        return ibv_pd.handle$get(segment());
    }

    public void setContext(final MemoryAddress value) {
        ibv_pd.context$set(segment(), value);
    }

    public void setHandle(final int value) {
        ibv_pd.handle$set(segment(), value);
    }
}
