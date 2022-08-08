package de.hhu.bsinfo.infinileap.engine.util;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class MemorySegmentOutputStream extends OutputStream {

    private static final VarHandle BYTE_HANDLE = MethodHandles.memorySegmentViewVarHandle(ValueLayout.JAVA_BYTE);

    private MemorySegment segment;

    private int index;

    private MemorySegmentOutputStream(MemorySegment segment) {
        this.segment = segment;
        this.index = 0;
    }

    @Override
    public void write(int b) throws IOException {
        BYTE_HANDLE.set(segment, index++, (byte) b);
    }

    public void reset(MemorySegment segment) {
        reset(segment, 0);
    }

    public int bytesWritten() {
        return index;
    }

    public void reset(MemorySegment segment, int offset) {
        this.segment = segment;
        this.index = offset;
    }

    public static MemorySegmentOutputStream wrap(MemorySegment segment) {
        return new MemorySegmentOutputStream(segment);
    }
}
