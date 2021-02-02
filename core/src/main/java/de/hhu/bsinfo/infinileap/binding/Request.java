package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;

import java.io.Closeable;

import static org.openucx.ucx_h.ucp_request_check_status;
import static org.openucx.ucx_h.ucp_request_free;

public class Request implements Closeable {

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

    public boolean hasError() {
        return Status.isError(address);
    }

    public boolean hasStatus(Status status) {
        return Status.is(address, status);
    }

    static Request of(MemoryAddress address) {
        return new Request(address);
    }

    @Override
    public void close() {
        ucp_request_free(address);
    }
}
