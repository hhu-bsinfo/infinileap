package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.util.flag.ByteFlag;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import de.hhu.bsinfo.infinileap.util.flag.ShortFlag;

public final class BitMask {

    private BitMask() {}

    @SafeVarargs
    public static <T extends Enum<T> & ByteFlag> byte byteOf(final T... flags) {
        byte mask = 0;
        for(var flag : flags) {
            mask |= flag.getValue();
        }
        return mask;
    }

    @SafeVarargs
    public static <T extends Enum<T> & ShortFlag> short shortOf(final T... flags) {
        short mask = 0;
        for(var flag : flags) {
            mask |= flag.getValue();
        }
        return mask;
    }

    @SafeVarargs
    public static <T extends Enum<T> & IntegerFlag> int intOf(final T... flags) {
        int mask = 0;
        for(var flag : flags) {
            mask |= flag.getValue();
        }
        return mask;
    }

    @SafeVarargs
    public static <T extends Enum<T> & LongFlag> long longOf(final T... flags) {
        long mask = 0;
        for(var flag : flags) {
            mask |= flag.getValue();
        }
        return mask;
    }

    public static <T extends Enum<T> & ByteFlag> boolean isSet(final byte mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }

    public static <T extends Enum<T> & ShortFlag> boolean isSet(final short mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }

    public static <T extends Enum<T> & IntegerFlag> boolean isSet(final int mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }

    public static <T extends Enum<T> & LongFlag> boolean isSet(final long mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }
}
