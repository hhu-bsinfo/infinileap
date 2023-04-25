package de.hhu.bsinfo.infinileap.binding;

import java.lang.foreign.MemoryAddress;
import org.junit.jupiter.api.Test;

import java.lang.foreign.MemorySegment;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

class StatusTest {

    @Test
    public void testConversion() {
        var expected = Status.values();
        var actual = Arrays.stream(Status.values())
                .mapToInt(Status::value)
                .mapToObj(Status::of)
                .toArray(Status[]::new);

        assertThat(actual).containsExactly(expected);
    }

    @Test
    public void testIsError() {
        final var status = MemorySegment.ofAddress(Status.IO_ERROR.value());
        assertThat(Status.isError(status)).isTrue();
    }

    @Test
    public void testIsNotError() {
        final var status = MemorySegment.ofAddress(Status.OK.value());
        assertThat(Status.isError(status)).isFalse();
    }

    @Test
    public void testIsStatus() {
        var actual = Arrays.stream(Status.values())
                .mapToInt(Status::value)
                .mapToObj(MemorySegment::ofAddress)
                .map(Status::isStatus);

        assertThat(actual).containsOnly(Boolean.TRUE);
    }

    @Test
    public void testIsNotStatus() {
        var status = MemorySegment.ofAddress(0x0000151BDD168610L);
        assertThat(Status.isStatus(status)).isFalse();
    }
}