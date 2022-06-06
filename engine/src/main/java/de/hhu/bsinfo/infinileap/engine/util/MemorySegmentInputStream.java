package de.hhu.bsinfo.infinileap.engine.util;

import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ValueLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.VarHandle;

public class MemorySegmentInputStream extends InputStream {

    private static final VarHandle BYTE_HANDLE = MemoryHandles.varHandle(ValueLayout.JAVA_BYTE);

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
