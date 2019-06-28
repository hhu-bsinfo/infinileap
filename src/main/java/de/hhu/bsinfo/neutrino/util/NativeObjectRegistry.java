package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NativeObjectRegistry {

    private static Map<Long, NativeObject> objectMap = new ConcurrentHashMap<>();

    private NativeObjectRegistry() {}

    public static void registerObject(NativeObject object) {
        objectMap.put(object.getHandle(), object);
    }

    public static void deregisterObject(NativeObject object) {
        objectMap.remove(object.getHandle());
    }

    public static NativeObject getObject(long handle) {
        return objectMap.get(handle);
    }
}
