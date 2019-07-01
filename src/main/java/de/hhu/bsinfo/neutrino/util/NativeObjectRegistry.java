package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NativeObjectRegistry {

    private static Map<Long, NativeObject> objectMap = new ConcurrentHashMap<>();

    private NativeObjectRegistry() {}

    public static <T extends NativeObject> void registerObject(@Nullable T object) {
        if(object != null) {
            objectMap.put(object.getHandle(), object);
        }
    }

    public static <T extends NativeObject> void deregisterObject(@Nullable T object) {
        if(object != null) {
            objectMap.remove(object.getHandle());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends NativeObject> T getObject(long handle) {
        return (T) objectMap.get(handle);
    }
}
