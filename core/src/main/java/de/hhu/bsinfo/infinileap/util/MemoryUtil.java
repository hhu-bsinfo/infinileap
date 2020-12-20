package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.*;

public class MemoryUtil {

    private static final int LINE_LENGTH = 16;

    private static final String LINE_SEPARATOR_CHARACTER = "-";
    private static final String COLUMN_SEPARATOR_CHARACTER = "|";

    private static final String HEXDUMP_HEADER = "  OFFSET  | 0  1  2  3  4  5  6  7   8  9  A  B  C  D  E  F|   ANSI ASCII   ";
    private static final String LINE_SEPARATOR = LINE_SEPARATOR_CHARACTER.repeat(HEXDUMP_HEADER.length());

    public static MemorySegment allocateMemory(long capacity) {
        return MemorySegment.ofNativeRestricted().asSlice(
                CLinker.allocateMemoryRestricted(capacity), capacity);
    }

    public static void freeMemory(Addressable addressable) {
        CLinker.freeMemoryRestricted(addressable.address());
    }

    public static MemorySegment createSegment(MemoryAddress address, long capacity) {
        return MemorySegment.ofNativeRestricted().asSlice(address, capacity);
    }

    public static void dump(MemorySegment segment) {

        var bytes = segment.byteSize();
        var offset = 0L;

        System.out.println(HEXDUMP_HEADER);
        System.out.println(LINE_SEPARATOR);

        int i;
        while (bytes > 0) {

            var length = bytes >= LINE_LENGTH ? LINE_LENGTH : bytes;

            // Print memory address
            System.out.printf(" %08X |", offset);

            // Print bytes
            for (i = 0; i < LINE_LENGTH; i++) {
                if (i < length) {
                    System.out.printf("%02X", MemoryAccess.getByteAtOffset(segment, offset + i));
                } else {
                    System.out.print("  ");
                }

                if (i == 7) {
                    System.out.print("  ");
                } else if (i != LINE_LENGTH - 1) {
                    System.out.print(" ");
                }
            }

            System.out.print(COLUMN_SEPARATOR_CHARACTER);

            // Print characters
            for (i = 0; i < LINE_LENGTH; i++) {
                if (i < length) {
                    System.out.printf("%c", sanitize(MemoryAccess.getByteAtOffset(segment, offset + i)));
                } else {
                    System.out.print(" ");
                }
            }

            System.out.println();

            offset += length;
            bytes -= length;
        }
    }

    private static char sanitize(byte value) {
        if (value < 0x40 || value > 0x7E) {
            return '.';
        }

        return (char) value;
    }
}
