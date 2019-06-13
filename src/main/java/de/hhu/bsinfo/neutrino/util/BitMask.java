package de.hhu.bsinfo.neutrino.util;

public final class BitMask {

    private BitMask() {}

    @SafeVarargs
    public static <T extends Enum<T> & Flag> byte byteOf(final T... flags) {
        byte mask = 0;
        for(var flag : flags) {
            mask |= (byte) flag.getValue();
        }
        return mask;
    }

    @SafeVarargs
    public static <T extends Enum<T> & Flag> short shortOf(final T... flags) {
        short mask = 0;
        for(var flag : flags) {
            mask |= (short) flag.getValue();
        }
        return mask;
    }

    @SafeVarargs
    public static <T extends Enum<T> & Flag> int intOf(final T... flags) {
        int mask = 0;
        for(var flag : flags) {
            mask |= flag.getValue();
        }
        return mask;
    }

    @SafeVarargs
    public static <T extends Enum<T> & Flag> long longOf(final T... flags) {
        int mask = 0;
        for(var flag : flags) {
            mask |= flag.getValue();
        }
        return mask;
    }

    public static <T extends Enum<T> & Flag> boolean isSet(final byte mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }

    public static <T extends Enum<T> & Flag> boolean isSet(final short mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }

    public static <T extends Enum<T> & Flag> boolean isSet(final int mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }

    public static <T extends Enum<T> & Flag> boolean isSet(final long mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }
}
