package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.*;

import java.io.PrintStream;

public class MemoryUtil {

    private static final int LINE_LENGTH = 16;

    private static final String LINE_SEPARATOR_CHARACTER = "-";
    private static final String COLUMN_SEPARATOR_CHARACTER = "|";

    private static final String HEXDUMP_HEADER = "  OFFSET  | 0  1  2  3  4  5  6  7   8  9  A  B  C  D  E  F|   ANSI ASCII   ";
    private static final String LINE_SEPARATOR = LINE_SEPARATOR_CHARACTER.repeat(HEXDUMP_HEADER.length());



    public static MemorySegment allocateMemory(long capacity) {
        return MemorySegment.globalNativeSegment().asSlice(
                CLinker.allocateMemory(capacity), capacity);
    }

    public static void freeMemory(Addressable addressable) {
        CLinker.freeMemory(addressable.address());
    }

    public static MemorySegment wrap(MemoryAddress address, long capacity) {
        return MemorySegment.globalNativeSegment().asSlice(address, capacity);
    }

    public static MemorySegment allocate(MemoryLayout layout) {
        return MemorySegment.allocateNative(layout, ResourceScope.newSharedScope());
    }

    public static void dump(MemorySegment segment) {
        dump(segment, System.out);
    }

    public static void dump(MemorySegment segment, PrintStream stream) {
        var bytes = segment.byteSize();
        var offset = 0L;

        stream.println(HEXDUMP_HEADER);
        stream.println(LINE_SEPARATOR);

        int i;
        while (bytes > 0) {

            var length = bytes >= LINE_LENGTH ? LINE_LENGTH : bytes;

            // Print memory address
            stream.printf(" %08X |", offset);

            // Print bytes
            for (i = 0; i < LINE_LENGTH; i++) {
                if (i < length) {
                    stream.printf("%02X", MemoryAccess.getByteAtOffset(segment, offset + i));
                } else {
                    stream.print("  ");
                }

                if (i == 7) {
                    stream.print("  ");
                } else if (i != LINE_LENGTH - 1) {
                    stream.print(" ");
                }
            }

            stream.print(COLUMN_SEPARATOR_CHARACTER);

            // Print characters
            for (i = 0; i < LINE_LENGTH; i++) {
                if (i < length) {
                    stream.printf("%c", sanitize(MemoryAccess.getByteAtOffset(segment, offset + i)));
                } else {
                    stream.print(" ");
                }
            }

            stream.println();

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
