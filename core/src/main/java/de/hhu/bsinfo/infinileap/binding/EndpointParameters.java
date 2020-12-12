package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h.*;

public class EndpointParameters extends NativeObject {

    public EndpointParameters() {
        super(ucp_ep_params_t.allocate());
    }

    private EndpointParameters(MemoryAddress address) {
        super(address, ucp_ep_params_t.$LAYOUT());
    }
}
