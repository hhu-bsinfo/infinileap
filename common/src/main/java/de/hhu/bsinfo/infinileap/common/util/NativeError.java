package de.hhu.bsinfo.infinileap.common.util;

import java.lang.foreign.*;

import static org.unix.Linux.*;


public class NativeError {

    private static final MemorySegment ERRNO = MemorySegment.ofAddress(
            __errno_location(), ValueLayout.ADDRESS.byteSize(), MemorySession.global());

    public static final int OK = 0;
    public static final int ERROR = -1;

    public static String getMessage() {
        return strerror(getCode()).getUtf8String(0L);
    }

    public static int getCode() {
        return ERRNO.get(ValueLayout.JAVA_INT, 0L);
    }
}
