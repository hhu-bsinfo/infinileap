package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;

import java.io.Closeable;

import static org.openucx.Communication.*;

/*
 * TODO(krakowski):
 *   This class is not used at the moment because of garbage collector pressure.
 *   Once primitive classes are delivered this class is a good candidate for it.
 */

public /* primitive */ class Request implements Closeable {

    public enum State {
        ERROR, PENDING, COMPLETE
    }

    private final long handle;

    private Request(long handle) {
        this.handle = handle;
    }

    public State state() {
        if (hasError()) {
            return State.ERROR;
        }

        if (hasStatus(Status.OK)) {
            return State.COMPLETE;
        }

        if (Status.is(ucp_request_check_status(handle), Status.IN_PROGRESS)) {
            return State.PENDING;
        }

        return State.COMPLETE;
    }

    MemoryAddress address() {
        return MemoryAddress.ofLong(handle);
    }

    public boolean hasError() {
        return Status.isError(handle);
    }

    public boolean hasStatus(Status status) {
        return handle == status.value();
    }

    static Request of(long address) {
        return new Request(address);
    }

    public void cancel(Worker worker) {
        if (!Status.isStatus(handle)) {
            ucp_request_cancel(Parameter.of(worker), handle);
            ucp_request_free(handle);
        }
    }

    public void release() {
        close();
    }

    @Override
    public void close() {
        if (!Status.isStatus(handle)) {
            ucp_request_free(handle);
        }
    }
}
