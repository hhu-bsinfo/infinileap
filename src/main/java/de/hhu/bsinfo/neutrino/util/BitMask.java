package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.util.flag.LongFlag;

public final class BitMask {

    private BitMask() {}

    @SafeVarargs
    public static <T extends Enum<T> & LongFlag> byte byteOf(final T... flags) {
        byte mask = 0;
        for(var flag : flags) {
            mask |= (byte) flag.getValue();
        }
        return mask;
    }

    @SafeVarargs
    public static <T extends Enum<T> & LongFlag> short shortOf(final T... flags) {
        short mask = 0;
        for(var flag : flags) {
            mask |= (short) flag.getValue();
        }
        return mask;
    }

    @SafeVarargs
    public static <T extends Enum<T> & LongFlag> int intOf(final T... flags) {
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

    public static <T extends Enum<T> & LongFlag> boolean isSet(final byte mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }

    public static <T extends Enum<T> & LongFlag> boolean isSet(final short mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }

    public static <T extends Enum<T> & LongFlag> boolean isSet(final int mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }

    public static <T extends Enum<T> & LongFlag> boolean isSet(final long mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }
}
