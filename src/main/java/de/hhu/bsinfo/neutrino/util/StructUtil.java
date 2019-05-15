package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.Result;
import de.hhu.bsinfo.neutrino.data.StructInformation;

public class StructUtil {

    static {
        System.loadLibrary("neutrino");
    }

    private static native void getDeviceAttributes(long resultHandle);
    private static native void getPortAttributes(long resultHandle);

    public static StructInformation getDeviceAttribtues() {
        var result = new Result();
        getDeviceAttributes(result.getHandle());
        return new StructInformation(result.getResultHandle());
    }

    public static StructInformation getPortAttributes() {
        var result = new Result();
        getPortAttributes(result.getHandle());
        return new StructInformation(result.getResultHandle());
    }
}
