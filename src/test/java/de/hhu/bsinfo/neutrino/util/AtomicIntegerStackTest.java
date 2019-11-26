package de.hhu.bsinfo.neutrino.util;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


class AtomicIntegerStackTest {

    private static final int DEFAULT_SIZE = 1024;

    @Test
    public void testSingleThreaded() {
        var stack = new AtomicIntegerStack(DEFAULT_SIZE);
        for (int i = 0; i < DEFAULT_SIZE; i++) {
            assertThat(stack.push(i)).isTrue();
        }

        for (int i = 1023; i >= 0; i--) {
            assertThat(stack.pop()).isEqualTo(i);
        }
    }

    @Test
    public void testMultiThreaded() {
        var stack = new AtomicIntegerStack(DEFAULT_SIZE);

        IntStream.rangeClosed(0, 1023)
                .parallel()
                .forEach(stack::push);

        IntStream.generate(stack::pop)
                .limit(1023)
                .parallel()
                .forEach(value -> assertThat(value).isBetween(0, 1023));
    }
}