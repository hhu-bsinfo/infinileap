package de.hhu.bsinfo.infinileap.engine.util;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class MemorySegmentInputStream extends InputStream {

    private static final VarHandle BYTE_HANDLE = MethodHandles.memorySegmentViewVarHandle(ValueLayout.JAVA_BYTE);

    private MemorySegment segment;

    private long index = 0L;

    public void wrap(MemorySegment segment) {
        this.segment = segment;
        this.index = 0L;
    }

    @Override
    public int read() throws IOException {
        return (int) BYTE_HANDLE.get(segment, index++);
    }
}
