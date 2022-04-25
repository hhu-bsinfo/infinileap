package de.hhu.bsinfo.infinileap.common.memory;

import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ValueLayout;

import java.util.concurrent.ThreadLocalRandom;

public class RandomBytes {

    public static void fill(MemorySegment segment) {
        var random = ThreadLocalRandom.current();
        for (long position = 0L, size = segment.byteSize(); position < size;) {
            for (int value = random.nextInt(), n = Math.min((int) (size - position), Integer.SIZE/Byte.SIZE); n-- > 0; value >>= Byte.SIZE) {
                segment.set(ValueLayout.JAVA_BYTE, position++, (byte) value);
            }
        }
    }
}
