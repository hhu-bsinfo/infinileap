package de.hhu.bsinfo.infinileap.example.util;

import de.hhu.bsinfo.infinileap.binding.Tag;

public class Constants {
    public static final int DEFAULT_PORT = 2998;

    public static final Tag TAG_BENCHMARK_OPCODE        = Tag.of(0x01);
    public static final Tag TAG_BENCHMARK_DETAILS       = Tag.of(0x02);
    public static final Tag TAG_BENCHMARK_DESCRIPTOR    = Tag.of(0x04);
    public static final Tag TAG_BENCHMARK_MESSAGE       = Tag.of(0x08);

    public static final byte LAST_MESSAGE = (byte) 0xFF;

    public static final int ATOMIC_32_BIT = 4;
    public static final int ATOMIC_64_BIT = 8;
}
