package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.*;

import static org.unix.Linux.*;


public class NativeError {

    private static final MemorySegment ERRNO = MemorySegment.ofAddressNative(
            __errno_location(), ValueLayout.ADDRESS.byteSize(), ResourceScope.globalScope());

    public static final int OK = 0;
    public static final int ERROR = -1;

    public static String getMessage() {
        return strerror(getCode()).getUtf8String(0L);
    }

    public static int getCode() {
        return ERRNO.get(ValueLayout.JAVA_INT, 0L);
    }
}
