package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.StructInformation;

import de.hhu.bsinfo.neutrino.verbs.Verbs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructUtil {

    static {
        System.loadLibrary("neutrino");
    }

    private StructUtil() {
    }

    private static native void getStructInformation(String identifier, long resultHandle);

    private static final Map<String, StructInformation> CACHE = new HashMap<>();

    public static StructInformation getInfo(final String identifier) {
        return CACHE.computeIfAbsent(identifier, key -> {
            var result = (Result) Verbs.getPoolableInstance(Result.class);
            getStructInformation(identifier, result.getHandle());
            if (result.isError()) {
                throw new IllegalArgumentException(String.format("No struct information found for %s", identifier));
            }

            result.releaseInstance();
            return result.get(StructInformation::new);
        });
    }

    public static <T extends NativeObject> List<T> wrap(final ReferenceFactory<T> factory, final long handle, final int size, final int length) {
        var result = new ArrayList<T>();
        for (long index = 0; index < length; index++) {
            result.add(factory.newInstance(handle + index * size));
        }
        return result;
    }
}
