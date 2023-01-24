package de.hhu.bsinfo.infinileap.common.memory;

import java.lang.foreign.*;

import de.hhu.bsinfo.infinileap.common.util.MemorySegments;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class MemoryUtil {

    private static final int LINE_LENGTH = 16;

    private static final String LINE_SEPARATOR_CHARACTER = "-";
    private static final String COLUMN_SEPARATOR_CHARACTER = "|";

    private static final String HEXDUMP_HEADER = "  OFFSET  | 0  1  2  3  4  5  6  7   8  9  A  B  C  D  E  F|   ANSI ASCII   ";
    private static final String LINE_SEPARATOR = LINE_SEPARATOR_CHARACTER.repeat(HEXDUMP_HEADER.length());


    public static MemorySegment wrap(long address, long capacity) {
        return MemorySegment.ofAddress(address, capacity, SegmentScope.global());
    }

    public static MemorySegment allocate(MemoryLayout layout, SegmentScope session) {
        return MemorySegment.allocateNative(layout, session);
    }

    public static MemorySegment allocate(long size, MemoryAlignment alignment, SegmentScope session) {
        return MemorySegment.allocateNative(size, alignment.value(), session);
    }

    public static void dump(MemorySegment base, long length) {
        dump(base, length, null);
    }

    public static void dump(MemorySegment base, long length, String title) {
        dump(base, length, title, System.out);
    }

    public static void dump(MemorySegment base, long length, String title, PrintStream stream) {
        try (var arena = Arena.openConfined()) {
            dump(MemorySegment.ofAddress(base.address(), length, arena.scope()), title, stream);
        }
    }

    public static long nativeAddress(MemorySegment segment) {
        return MemorySegments.NULL.segmentOffset(segment);
    }

    public static void dump(MemorySegment segment) {
        dump(segment, null);
    }

    public static void dump(MemorySegment segment, @Nullable String title) {
        dump(segment, title, System.out);
    }

    public static void dump(MemorySegment segment, @Nullable String title, PrintStream stream) {

        stream.println();

        if (title != null) {
            stream.println(LINE_SEPARATOR);
            stream.println(" ".repeat((LINE_SEPARATOR.length() - title.length()) / 2).concat(title));
            stream.println(LINE_SEPARATOR);
        }

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
                    stream.printf("%02X", segment.get(ValueLayout.JAVA_BYTE, offset + i));
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
                    stream.printf("%c", sanitize(segment.get(ValueLayout.JAVA_BYTE, offset + i)));
                } else {
                    stream.print(" ");
                }
            }

            stream.println();

            offset += length;
            bytes -= length;
        }

        stream.println();
    }

    private static char sanitize(byte value) {
        if (value < 0x30 || value > 0x7E) {
            return '.';
        }

        return (char) value;
    }
}
