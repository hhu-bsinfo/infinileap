package de.hhu.bsinfo.infinileap.engine.message;

import de.hhu.bsinfo.infinileap.binding.Identifier;
import jdk.incubator.foreign.MemorySegment;

public record Message(
        Identifier identifier,
        MemorySegment header,
        MemorySegment body
) {}
