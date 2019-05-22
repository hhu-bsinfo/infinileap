package de.hhu.bsinfo.neutrino.util;

public final class BitMask {

    private BitMask() {}

    @SafeVarargs
    public static <T extends Enum<T> & Flag> int of(final T... flags) {
        int mask = 0;
        for(var flag : flags) {
            mask |= flag.getValue();
        }
        return mask;
    }

    public static <T extends Enum<T> & Flag> boolean isSet(final int mask, final T flag) {
        return (mask & flag.getValue()) != 0;
    }
}
