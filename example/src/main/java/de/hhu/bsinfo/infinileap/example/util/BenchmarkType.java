package de.hhu.bsinfo.infinileap.example.util;

import java.util.Arrays;

public enum BenchmarkType {
    RDMA_THROUGHPUT             (0x01),
    RDMA_LATENCY                (0x02),
    MESSAGING_THROUGHPUT        (0x03),
    MESSAGING_LATENCY           (0x04);

    private final int value;

    BenchmarkType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static BenchmarkType from(int value) {
        return Arrays.stream(values())
                .filter(it -> it.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
