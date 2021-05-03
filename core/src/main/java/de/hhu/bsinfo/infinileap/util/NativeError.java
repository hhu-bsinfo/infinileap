package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.*;

import static org.unix.Linux.*;


public class NativeError {

    private static final MemorySegment ERRNO = MemorySegment.globalNativeSegment()
            .asSlice(__errno_location(), CLinker.C_POINTER.byteSize());

    public static final int OK = 0;
    public static final int ERROR = -1;

    public static String getMessage() {
        return CLinker.toJavaString(strerror(getCode()));
    }

    public static int getCode() {
        return MemoryAccess.getInt(ERRNO);
    }
}
