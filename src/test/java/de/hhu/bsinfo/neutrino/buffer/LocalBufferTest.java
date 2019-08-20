package de.hhu.bsinfo.neutrino.buffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class LocalBufferTest {

    private static final long CAPACITY = 1024;
    private LocalBuffer buffer;

    @BeforeEach
    public void setupBuffer() {
        buffer = LocalBuffer.allocate(CAPACITY);
    }

    @Test
    void testBoundsCheck() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> buffer.get(CAPACITY));

        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> buffer.getShort(CAPACITY - Byte.BYTES));

        assertThatCode(() -> buffer.get(CAPACITY - 1))
                .doesNotThrowAnyException();
    }
}