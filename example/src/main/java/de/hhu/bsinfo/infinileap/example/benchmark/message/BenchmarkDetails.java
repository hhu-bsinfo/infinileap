package de.hhu.bsinfo.infinileap.example.benchmark.message;

import de.hhu.bsinfo.infinileap.binding.NativeObject;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;

import java.lang.invoke.VarHandle;

import static jdk.incubator.foreign.CLinker.C_LONG;

public class BenchmarkDetails extends NativeObject {

    private static final MemoryLayout LAYOUT = MemoryLayout.ofStruct(
            C_LONG.withName("buffer_size")
    );

    private static final VarHandle BUFFER_SIZE =
            LAYOUT.varHandle(long.class, MemoryLayout.PathElement.groupElement("buffer_size"));

    public BenchmarkDetails() {
        super(MemorySegment.allocateNative(LAYOUT));
    }

    public void setBufferSize(long size) {
        BUFFER_SIZE.set(segment(), size);
    }

    public long getBufferSize() {
        return (long) BUFFER_SIZE.get(segment());
    }
}
