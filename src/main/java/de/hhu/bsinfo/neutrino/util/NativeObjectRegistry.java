package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NativeObjectRegistry {

    private static final Map<Long, NativeObject> OBJECT_MAP = new ConcurrentHashMap<>();

    private NativeObjectRegistry() {}

    public static <T extends NativeObject> void registerObject(@Nullable T object) {
        if(object != null) {
            OBJECT_MAP.put(object.getHandle(), object);
        }
    }

    public static <T extends NativeObject> void deregisterObject(@Nullable T object) {
        if(object != null) {
            OBJECT_MAP.remove(object.getHandle());
        }
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <T extends NativeObject> T getObject(long handle) {
        return (T) OBJECT_MAP.get(handle);
    }
}
