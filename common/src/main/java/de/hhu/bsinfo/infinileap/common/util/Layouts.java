package de.hhu.bsinfo.infinileap.common.util;

import java.lang.foreign.ValueLayout;

public class Layouts {

    private static final int BYTE_BITS = 8;

    public static final ValueLayout.OfLong JAVA_LONG_UNALIGNED = ValueLayout.JAVA_LONG.withBitAlignment(BYTE_BITS);

}
