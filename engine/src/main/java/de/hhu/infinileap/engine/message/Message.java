package de.hhu.infinileap.engine.message;

import jdk.incubator.foreign.MemorySegment;

public record Message(
        MemorySegment header,
        MemorySegment body
) {}
