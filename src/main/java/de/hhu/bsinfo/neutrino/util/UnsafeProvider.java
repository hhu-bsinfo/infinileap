package de.hhu.bsinfo.neutrino.util;

import java.lang.reflect.Field;

/**
 * Utility class for accessing sun.misc.Unsafe using Reflection.
 */
@SuppressWarnings({"sunapi"})
public final class UnsafeProvider {

    @SuppressWarnings("UseOfSunClasses")
    private static final sun.misc.Unsafe UNSAFE = initUnsafe();

    private UnsafeProvider() {}

    @SuppressWarnings("ProhibitedExceptionThrown")
    private static sun.misc.Unsafe initUnsafe() {
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (sun.misc.Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an instance of sun.misc.Unsafe.
     *
     * @return An instance of sun.misc.Unsafe.
     */
    public static sun.misc.Unsafe getUnsafe() {
        return UNSAFE;
    }
}