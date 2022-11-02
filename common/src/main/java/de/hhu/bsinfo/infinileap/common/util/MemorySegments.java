package de.hhu.bsinfo.infinileap.common.util;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

public class MemorySegments {

    public static final MemorySegment NULL = MemorySegment.ofAddress(MemoryAddress.NULL, 0L, MemorySession.global());
}
