package de.hhu.bsinfo.infinileap.util;

public class NativeBoolean {

    public static final byte TRUE = 1;

    public static final byte FALSE = 0;

    public static byte of(boolean value) {
        return value ? TRUE : FALSE;
    }
}
