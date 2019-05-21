package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.StructInformation;

import de.hhu.bsinfo.neutrino.verbs.Verbs;
import java.util.ArrayList;
import java.util.List;

public class StructUtil {

    static {
        System.loadLibrary("neutrino");
    }

    private static native void getStructInformation(String identifier, long resultHandle);

    public static StructInformation getInfo(String identifier) {
        var result = Verbs.getResultPool().getInstance();

        getStructInformation(identifier, result.getHandle());
        if (result.isError()) {
            throw new IllegalArgumentException(String.format("No struct information found for %s", identifier));
        }

        Verbs.getResultPool().returnInstance(result);
        return result.get(StructInformation::new);
    }

    public static <T extends NativeObject> List<T> wrap(final ReferenceFactory<T> factory, final long handle, final int size, final int length) {
        var result = new ArrayList<T>();
        for (long index = 0; index < length; index++) {
            result.add(factory.newInstance(handle + index * size));
        }
        return result;
    }
}
