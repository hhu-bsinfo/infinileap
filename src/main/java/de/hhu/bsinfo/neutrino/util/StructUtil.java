package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.struct.MemberInformation;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;

import java.util.ArrayList;
import java.util.List;

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

    public static <T extends Struct> List<T> wrap(final StructFactory<T> factory, final long handle, final int size, final int length) {
        var result = new ArrayList<T>();
        for (long index = 0; index < length; index++) {
            result.add(factory.newInstance(handle + index * size));
        }
        return result;
    }
}
