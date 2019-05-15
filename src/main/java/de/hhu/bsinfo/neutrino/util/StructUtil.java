package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.Result;
import de.hhu.bsinfo.neutrino.data.StructInformation;

public class StructUtil {

    static {
        System.loadLibrary("neutrino");
    }

    private static native void getStructInformation(String identifier, long resultHandle);

    public static StructInformation getInfo(String identifier) {
        var result = new Result();
        getStructInformation(identifier, result.getHandle());
        if (result.isError()) {
            throw new IllegalArgumentException(String.format("No struct information found for %s", identifier));
        }
        return new StructInformation(result.getResultHandle());
    }
}
