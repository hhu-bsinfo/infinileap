package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.Parameter;
import jdk.incubator.foreign.MemoryAddress;
import org.openucx.ucx_h;

import static org.openucx.ucx_h.ucp_request_check_status;

public class Request {

    // TODO(krakowski):
    //  ucp_request_free must be called to release a request handle

    private final MemoryAddress address;

    private Request(MemoryAddress address) {
        this.address = address;
    }

    public boolean isCompleted() {
        return Status.IN_PROGRESS.isNot(ucp_request_check_status(address));
    }

    MemoryAddress address() {
        return address;
    }

    static Request of(MemoryAddress address) {
        return new Request(address);
    }
}
