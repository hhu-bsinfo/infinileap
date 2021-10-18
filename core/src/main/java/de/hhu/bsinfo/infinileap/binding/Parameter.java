package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.binding.NativeObject;
import jdk.incubator.foreign.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

public class Parameter {

    static Addressable of(@NotNull String value, ResourceScope scope) {
        return SegmentAllocator.nativeAllocator(scope).allocateUtf8String(value);
    }

    static Addressable ofNullable(@Nullable String value, ResourceScope scope) {
        return value != null ? of(value, scope) : MemoryAddress.NULL;
    }

    static Addressable of(@NotNull NativeObject object) {
        return object.address();
    }

    static Addressable ofNullable(@Nullable NativeObject object) {
        return object != null ? of(object) : MemoryAddress.NULL;
    }
}
