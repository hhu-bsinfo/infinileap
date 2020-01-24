package de.hhu.bsinfo.neutrino.util;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


class AtomicIntegerStackTest {

    private static final int DEFAULT_SIZE = 1024;

    @Test
    public void testSingleThreaded() {
        var stack = new AtomicIntegerStack();
        for (int i = 0; i < DEFAULT_SIZE; i++) {
            stack.push(i);
        }

        for (int i = 1023; i >= 0; i--) {
            assertThat(stack.pop()).isEqualTo(i);
        }
    }
}