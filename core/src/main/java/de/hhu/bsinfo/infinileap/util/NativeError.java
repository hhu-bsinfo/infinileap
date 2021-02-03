package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.*;

import org.openucx.ucx_h;

import static org.openucx.ucx_h.__errno_location;


public class NativeError {

    private static final MemorySegment ERRNO = MemorySegment.ofNativeRestricted()
            .asSlice(__errno_location(), CLinker.C_POINTER.byteSize());

    public static final int OK = 0;
    public static final int ERROR = -1;

    public static String getMessage() {
        return CLinker.toJavaStringRestricted(ucx_h.strerror(getCode()));
    }

    public static int getCode() {
        return MemoryAccess.getInt(ERRNO);
    }
}
