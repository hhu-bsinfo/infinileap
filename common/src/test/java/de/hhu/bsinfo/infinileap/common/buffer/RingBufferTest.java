package de.hhu.bsinfo.infinileap.common.buffer;

import de.hhu.bsinfo.infinileap.common.memory.MemoryAlignment;
import jdk.incubator.foreign.ValueLayout;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class RingBufferTest {

    @Test
    public void testWriteRead() {
        final var expected = 0x42L;
        final var buffer = new RingBuffer(MemoryAlignment.PAGE.value());

        // Claim space and write value
        final var segment = buffer.tryClaim((int) ValueLayout.JAVA_LONG.byteSize());
        segment.set(ValueLayout.JAVA_LONG, 0, expected);
        buffer.commitWrite(segment);

        // Read value from buffer
        final var readValue = new AtomicLong();
        final var bytesRead = buffer.read(((type, data, index, length) -> {
            assertEquals(length, ValueLayout.JAVA_LONG.byteSize());
            readValue.set(data.get(ValueLayout.JAVA_LONG, index));
        }), 1);

        // Commit read
        buffer.commitRead(bytesRead);

        // Compare actual value with expected value
        assertEquals(readValue.get(), expected);
    }

}