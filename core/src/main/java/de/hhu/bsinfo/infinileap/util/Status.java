package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.*;

import static org.linux.rdma.infinileap_h.__errno_location;
import static org.linux.rdma.infinileap_h.strerror;

public class Status {

    private static final MemorySegment ERRNO = MemorySegment.ofNativeRestricted(
            __errno_location(),
            CSupport.C_POINTER.byteSize(),
            null,
            null,
            null
    );

    public static final int OK = 0;

    public static String getErrorMessage() {
        return CSupport.toJavaStringRestricted(strerror(getError()));
    }

    public static int getError() {
        return MemoryAccess.getInt(ERRNO);
    }
}
