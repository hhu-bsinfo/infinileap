package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h.*;

public class RequestParameters extends NativeObject {

    public RequestParameters() {
        super(ucp_request_param_t.allocate());
    }

    private RequestParameters(MemoryAddress address) {
        super(address, ucp_request_param_t.$LAYOUT());
    }
}
