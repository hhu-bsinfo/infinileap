package de.hhu.bsinfo.neutrino.util;

import java.nio.ByteBuffer;

public class MemoryUtil {

    private MemoryUtil() {
    }

    public static native ByteBuffer wrap(long handle, int size);
    public static native long getAddress(ByteBuffer byteBuffer);
    public static native void free(long handle);
}
