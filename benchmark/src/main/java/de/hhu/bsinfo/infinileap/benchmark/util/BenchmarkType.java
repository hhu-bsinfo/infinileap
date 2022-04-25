package de.hhu.bsinfo.infinileap.benchmark.util;

public enum BenchmarkType {
    MEMORY_ACCESS               (0x01),
    MESSAGING                   (0x02),
    PINGPONG                    (0x03),
    ATOMIC                      (0x04);

    private final int value;

    BenchmarkType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
