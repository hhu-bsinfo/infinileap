package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;

import de.hhu.bsinfo.neutrino.verbs.Verbs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StructUtil {

    static {
        NativeLibrary.load("neutrino");
    }

    private StructUtil() {}

    private static native void getStructInformation(String identifier, long resultHandle);

    private static final Map<Class<? extends Struct>, StructInformation> CACHE = new ConcurrentHashMap<>();

    public static StructInformation getInfo(final Class<? extends Struct> structClass) {
        return CACHE.computeIfAbsent(structClass, StructUtil::getNativeInfo);
    }

    public static int getSize(final Class<? extends Struct> structClass) {
        return getInfo(structClass).size.get();
    }

    private static StructInformation getNativeInfo(final Class<? extends Struct> structClass) {
        var customStruct = structClass.getAnnotation(CustomStruct.class);
        if (customStruct != null) {
            return new StructInformation(customStruct);
        }

        var linkNative = structClass.getAnnotation(LinkNative.class);
        if (linkNative == null) {
            throw new IllegalArgumentException(String.format("%s is missing the %s annotation", structClass.getSimpleName(), LinkNative.class.getSimpleName()));
        }

        var result = Result.localInstance();
        getStructInformation(linkNative.value(), result.getHandle());
        if (result.isError()) {

            throw new IllegalArgumentException(String.format("No struct information found for %s", linkNative.value()));
        }


        return result.get(StructInformation::new);
    }

    public static <T extends NativeObject> List<T> wrap(final ReferenceFactory<T> factory, final long handle, final int size, final int length) {
        var result = new ArrayList<T>();
        for (long index = 0; index < length; index++) {
            result.add(factory.newInstance(handle + index * size));
        }
        return result;
    }

    private static final class EmptyStructInformation extends StructInformation {

        public EmptyStructInformation(long handle) {
            super(handle);
        }
    }
}
