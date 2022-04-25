package de.hhu.bsinfo.infinileap.benchmark.util;

import de.hhu.bsinfo.infinileap.binding.Tag;

public class Constants {
    public static final int DEFAULT_PORT = 2998;

    public static final Tag TAG_ZERO                    = Tag.of(0x00);

    public static final Tag TAG_BENCHMARK_OPCODE        = Tag.of(0x01);
    public static final Tag TAG_BENCHMARK_DETAILS       = Tag.of(0x02);
    public static final Tag TAG_BENCHMARK_DESCRIPTOR    = Tag.of(0x04);
    public static final Tag TAG_BENCHMARK_MESSAGE       = Tag.of(0x08);
    public static final Tag TAG_BENCHMARK_SIGNAL       = Tag.of(0x10);

    public static final byte LAST_MESSAGE = (byte) 0xFF;

    public static final int ATOMIC_32_BIT = 4;
    public static final int ATOMIC_64_BIT = 8;

    public static final int MAX_OUTSTANDING_REQUESTS = 32;

    public static final String[] BUFFER_SIZES_ALL = { "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096" };
}
