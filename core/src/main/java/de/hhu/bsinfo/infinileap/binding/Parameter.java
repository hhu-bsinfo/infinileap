package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import java.lang.foreign.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Parameter {

    static Addressable of(@NotNull String value, MemorySession session) {
        return SegmentAllocator.newNativeArena(session).allocateUtf8String(value);
    }

    static Addressable ofNullable(@Nullable String value, MemorySession session) {
        return value != null ? of(value, session) : MemoryAddress.NULL;
    }

    static Addressable of(@NotNull NativeObject object) {
        return object.address();
    }

    static Addressable ofNullable(@Nullable NativeObject object) {
        return object != null ? of(object) : MemoryAddress.NULL;
    }
}
