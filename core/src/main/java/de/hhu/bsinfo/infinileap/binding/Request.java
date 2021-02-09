package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;

import java.io.Closeable;

import static org.openucx.ucx_h.ucp_request_check_status;
import static org.openucx.ucx_h.ucp_request_free;

public class Request implements Closeable {

    public enum State {
        ERROR, PENDING, COMPLETE
    }

    private final MemoryAddress address;

    private Request(MemoryAddress address) {
        this.address = address;
    }

    public State state() {
        if (hasError()) {
            return State.ERROR;
        }

        if (hasStatus(Status.OK)) {
            return State.COMPLETE;
        }

        if (Status.is(ucp_request_check_status(address), Status.IN_PROGRESS)) {
            return State.PENDING;
        }

        return State.COMPLETE;
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
        if (!Status.isStatus(address)) {
            ucp_request_free(address);
        }
    }
}
