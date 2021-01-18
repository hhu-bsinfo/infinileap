package de.hhu.bsinfo.infinileap.binding;

import java.util.NoSuchElementException;

import static org.openucx.ucx_h.*;

public enum Status {

    /* Operation completed successfully */
    OK(UCS_OK()),

    /* Operation is queued and still in progress */
    IN_PROGRESS(UCS_INPROGRESS()),

    /* Failure codes */
    NO_MESSAGE(UCS_ERR_NO_MESSAGE()),
    NO_RESOURCE(UCS_ERR_NO_RESOURCE()),
    IO_ERROR(UCS_ERR_IO_ERROR()),
    NO_MEMORY(UCS_ERR_NO_MEMORY()),
    INVALID_PARAM(UCS_ERR_INVALID_PARAM()),
    UNREACHABLE(UCS_ERR_UNREACHABLE()),
    INVALID_ADDR(UCS_ERR_INVALID_ADDR()),
    NOT_IMPLEMENTED(UCS_ERR_NOT_IMPLEMENTED()),
    MESSAGE_TRUNCATED(UCS_ERR_MESSAGE_TRUNCATED()),
    NO_PROGRESS(UCS_ERR_NO_PROGRESS()),
    BUFFER_TOO_SMALL(UCS_ERR_BUFFER_TOO_SMALL()),
    NO_ELEM(UCS_ERR_NO_ELEM()),
    SOME_CONNECTS_FAILED(UCS_ERR_SOME_CONNECTS_FAILED()),
    NO_DEVICE(UCS_ERR_NO_DEVICE()),
    BUSY(UCS_ERR_BUSY()),
    CANCELED(UCS_ERR_CANCELED()),
    SHMEM_SEGMENT(UCS_ERR_SHMEM_SEGMENT()),
    ALREADY_EXISTS(UCS_ERR_ALREADY_EXISTS()),
    OUT_OF_RANGE(UCS_ERR_OUT_OF_RANGE()),
    TIMED_OUT(UCS_ERR_TIMED_OUT()),
    EXCEEDS_LIMIT(UCS_ERR_EXCEEDS_LIMIT()),
    UNSUPPORTED(UCS_ERR_UNSUPPORTED()),
    REJECTED(UCS_ERR_REJECTED()),
    NOT_CONNECTED(UCS_ERR_NOT_CONNECTED()),
    CONNECTION_RESET(UCS_ERR_CONNECTION_RESET()),

    FIRST_LINK_FAILURE(UCS_ERR_FIRST_LINK_FAILURE()),
    LAST_LINK_FAILURE(UCS_ERR_LAST_LINK_FAILURE()),
    FIRST_ENDPOINT_FAILURE(UCS_ERR_FIRST_ENDPOINT_FAILURE()),
    ENDPOINT_TIMEOUT(UCS_ERR_ENDPOINT_TIMEOUT()),
    LAST_ENDPOINT_FAILURE(UCS_ERR_LAST_ENDPOINT_FAILURE());

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

    final boolean isNot(int status) {
        return value != status;
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
