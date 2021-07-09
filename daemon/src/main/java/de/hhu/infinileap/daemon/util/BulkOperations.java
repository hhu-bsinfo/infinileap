package de.hhu.infinileap.daemon.util;

import jdk.incubator.foreign.MemorySegment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class BulkOperations {

    private BulkOperations() {}

    public static void copy(MemorySegment source, ByteBuffer target) {
        MemorySegment.ofByteBuffer(target).copyFrom(source);
    }

    public static void copy(ByteBuffer source, MemorySegment target) {
        target.copyFrom(MemorySegment.ofByteBuffer(source));
    }

    public static void copy(MemorySegment source, OutputStream target) throws IOException {
        target.write(source.toByteArray());
    }

    public static int copy(InputStream source, MemorySegment target) throws IOException {
        final var array = source.readAllBytes();
        if (array.length == 0) {
            return -1;
        }

        target.copyFrom(MemorySegment.ofArray(array));
        return array.length;
    }

    public static void copy(MemorySegment source, byte[] target) {
        MemorySegment.ofArray(target).copyFrom(source);
    }

    public static void copy(byte[] source, MemorySegment target) {
        target.copyFrom(MemorySegment.ofArray(source));
    }

    public static void copy(MemorySegment source, byte[] target, int targetIndex) {
        MemorySegment.ofArray(target).asSlice(targetIndex).copyFrom(source);
    }

    public static void copy(byte[] source, int sourceIndex, MemorySegment target, int length) {
        target.copyFrom(MemorySegment.ofArray(source).asSlice(sourceIndex, length));
    }

    public static void copy(MemorySegment source, MemorySegment target) {
        target.copyFrom(source);
    }

}
