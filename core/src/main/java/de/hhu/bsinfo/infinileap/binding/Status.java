package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAddress;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;

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
    LAST_ENDPOINT_FAILURE(UCS_ERR_LAST_ENDPOINT_FAILURE()),

    LAST(UCS_ERR_LAST());

    private static final int RANGE_MIN = Arrays.stream(Status.values()).mapToInt(Status::value).min().orElseThrow();
    private static final int RANGE_MAX = Arrays.stream(Status.values()).mapToInt(Status::value).max().orElseThrow();

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

    static boolean is(MemoryAddress address, Status status) {
        return isStatus(address) && status.is((int) address.toRawLongValue());
    }

    static boolean isStatus(MemoryAddress status) {
        return isStatus(status.toRawLongValue());
    }

    static boolean isStatus(long status) {
        return status >= RANGE_MIN && status <= RANGE_MAX;
    }

    static boolean isError(MemoryAddress status) {
        return isError(status.toRawLongValue());
    }

    static boolean isError(long status) {
        return status >= RANGE_MIN && status < 0;
    }

    static Status of(MemoryAddress status) {
        return of((int) status.toRawLongValue());
    }

    static Status of(int status) {
        var ret = Lookup.get(status);
        if (ret != null) {
            return ret;
        }

        throw new NoSuchElementException();
    }

    private static final class Lookup {

        private static final Status[] POSITIVE = populate(Range.POSITIVE);
        private static final Status[] NEGATIVE = populate(Range.NEGATIVE);

        static Status get(int status) {
            if (status >= 0) {
                return POSITIVE[status];
            }

            return NEGATIVE[-status];
        }

        private static Status[] populate(Range range) {
            var filtered = range.filter(Status.values());
            var size = Arrays.stream(filtered)
                    .mapToInt(Status::value)
                    .map(Math::abs)
                    .max().orElseThrow() + 1;

            var array = new Status[size];
            for (var status : filtered) {
                array[Math.abs(status.value())] = status;
            }

            return array;
        }
    }

    private enum Range {
        POSITIVE(statuses -> Arrays.stream(statuses).filter(status -> status.value() >= 0).toArray(Status[]::new)),
        NEGATIVE(statuses -> Arrays.stream(statuses).filter(status -> status.value() <  0).toArray(Status[]::new));

        private final Function<Status[], Status[]> filter;

        Range(Function<Status[], Status[]> filter) {
            this.filter = filter;
        }

        Status[] filter(Status[] statuses) {
            return filter.apply(statuses);
        }
    }
}
