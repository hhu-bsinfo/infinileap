package de.hhu.bsinfo.neutrino.data;

import java.nio.ByteBuffer;

public class MemoryUtil {

    public static native ByteBuffer wrap(long handle, int size);

    public static native long getAddress(ByteBuffer byteBuffer);
}
