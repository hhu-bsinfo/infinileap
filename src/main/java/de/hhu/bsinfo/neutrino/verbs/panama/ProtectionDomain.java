package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import org.linux.rdma.ibverbs_h.*;

public class ProtectionDomain extends Struct {

    public ProtectionDomain() {
        super(Cibv_pd::allocate);
    }

    public ProtectionDomain(MemoryAddress address) {
        super(Cibv_pd.$LAYOUT(), address);
    }
}
