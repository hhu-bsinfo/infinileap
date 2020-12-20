package de.hhu.bsinfo.infinileap.binding;

import org.openucx.ucx_h;

import java.util.NoSuchElementException;

public enum Status {

    /* Operation completed successfully */
    OK(ucx_h.UCS_OK()),

    /* Operation is queued and still in progress */
    IN_PROGRESS(ucx_h.UCS_INPROGRESS()),

    /* Failure codes */
    NO_MESSAGE(ucx_h.UCS_ERR_NO_MESSAGE()),
    NO_RESOURCE(ucx_h.UCS_ERR_NO_RESOURCE()),
    IO_ERROR(ucx_h.UCS_ERR_IO_ERROR()),
    NO_MEMORY(ucx_h.UCS_ERR_NO_MEMORY()),
    INVALID_PARAM(ucx_h.UCS_ERR_INVALID_PARAM()),
    UNREACHABLE(ucx_h.UCS_ERR_UNREACHABLE()),
    INVALID_ADDR(ucx_h.UCS_ERR_INVALID_ADDR()),
    NOT_IMPLEMENTED(ucx_h.UCS_ERR_NOT_IMPLEMENTED()),
    MESSAGE_TRUNCATED(ucx_h.UCS_ERR_MESSAGE_TRUNCATED()),
    NO_PROGRESS(ucx_h.UCS_ERR_NO_PROGRESS()),
    BUFFER_TOO_SMALL(ucx_h.UCS_ERR_BUFFER_TOO_SMALL()),
    NO_ELEM(ucx_h.UCS_ERR_NO_ELEM()),
    SOME_CONNECTS_FAILED(ucx_h.UCS_ERR_SOME_CONNECTS_FAILED()),
    NO_DEVICE(ucx_h.UCS_ERR_NO_DEVICE()),
    BUSY(ucx_h.UCS_ERR_BUSY()),
    CANCELED(ucx_h.UCS_ERR_CANCELED()),
    SHMEM_SEGMENT(ucx_h.UCS_ERR_SHMEM_SEGMENT()),
    ALREADY_EXISTS(ucx_h.UCS_ERR_ALREADY_EXISTS()),
    OUT_OF_RANGE(ucx_h.UCS_ERR_OUT_OF_RANGE()),
    TIMED_OUT(ucx_h.UCS_ERR_TIMED_OUT()),
    EXCEEDS_LIMIT(ucx_h.UCS_ERR_EXCEEDS_LIMIT()),
    UNSUPPORTED(ucx_h.UCS_ERR_UNSUPPORTED()),
    REJECTED(ucx_h.UCS_ERR_REJECTED()),
    NOT_CONNECTED(ucx_h.UCS_ERR_NOT_CONNECTED()),
    CONNECTION_RESET(ucx_h.UCS_ERR_CONNECTION_RESET()),

    FIRST_LINK_FAILURE(ucx_h.UCS_ERR_FIRST_LINK_FAILURE()),
    LAST_LINK_FAILURE(ucx_h.UCS_ERR_LAST_LINK_FAILURE()),
    FIRST_ENDPOINT_FAILURE(ucx_h.UCS_ERR_FIRST_ENDPOINT_FAILURE()),
    ENDPOINT_TIMEOUT(ucx_h.UCS_ERR_ENDPOINT_TIMEOUT()),
    LAST_ENDPOINT_FAILURE(ucx_h.UCS_ERR_LAST_ENDPOINT_FAILURE());

    private final int value;

    Status(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    final boolean is(int status) {
        return value == status;
    }

    static Status of(int status) {
        for (var value : values()) {
            if (value.is(status)) {
                return value;
            }
        }

        throw new NoSuchElementException();
    }
}
