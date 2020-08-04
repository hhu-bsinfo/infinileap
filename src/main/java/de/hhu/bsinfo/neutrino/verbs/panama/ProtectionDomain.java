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
}
