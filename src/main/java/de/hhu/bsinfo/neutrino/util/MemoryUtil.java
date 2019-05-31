package de.hhu.bsinfo.neutrino.util;

import java.nio.ByteBuffer;

public class MemoryUtil {

    private static final sun.misc.Unsafe UNSAFE = UnsafeProvider.getUnsafe();

    private MemoryUtil() {
    }

    public static native ByteBuffer wrap(long handle, int size);
    public static native long getAddress(ByteBuffer byteBuffer);
    public static native void free(long handle);

    public static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }
}
