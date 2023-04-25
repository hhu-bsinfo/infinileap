package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import java.lang.foreign.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Parameter {

    static MemorySegment of(@NotNull String value, SegmentAllocator allocator) {
        return allocator.allocateUtf8String(value);
    }

    static MemorySegment ofNullable(@Nullable String value, SegmentAllocator allocator) {
        return value != null ? of(value, allocator) : MemorySegment.NULL;
    }

    static MemorySegment of(@NotNull NativeObject object) {
        return object.segment();
    }

    static MemorySegment ofNullable(@Nullable NativeObject object) {
        return object != null ? of(object) : MemorySegment.NULL;
    }
}
