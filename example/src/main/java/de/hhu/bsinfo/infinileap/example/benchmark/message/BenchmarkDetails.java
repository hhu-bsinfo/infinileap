package de.hhu.bsinfo.infinileap.example.benchmark.message;

import de.hhu.bsinfo.infinileap.binding.NativeObject;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

import java.lang.invoke.VarHandle;

import static jdk.incubator.foreign.CLinker.*;

public class BenchmarkDetails extends NativeObject {

    public enum Mode {
        LATENCY((byte) 0x1),
        THROUGHPUT((byte) 0x2);

        private final byte mode;

        Mode(byte mode) {
            this.mode = mode;
        }

        public byte value() {
            return mode;
        }

        static Mode from(byte value) {
            return switch (value) {
                case 1 -> LATENCY;
                case 2 -> THROUGHPUT;
                default -> throw new IllegalArgumentException("Unknown benchmark mode");
            };
        }
    }

    private static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            C_LONG.withName("buffer_size"),
            C_INT.withName("op_count"),
            C_CHAR.withName("bench_mode")
    );

    private static final VarHandle BUFFER_SIZE =
            LAYOUT.varHandle(long.class, MemoryLayout.PathElement.groupElement("buffer_size"));

    private static final VarHandle OPERATION_COUNT =
            LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("op_count"));

    private static final VarHandle BENCHMARK_MODE =
            LAYOUT.varHandle(byte.class, MemoryLayout.PathElement.groupElement("bench_mode"));

    public BenchmarkDetails() {
        this(ResourceScope.newImplicitScope());
    }

    public BenchmarkDetails(ResourceScope scope) {
        super(MemorySegment.allocateNative(LAYOUT, scope));
    }

    public void setBufferSize(long size) {
        BUFFER_SIZE.set(segment(), size);
    }

    public long getBufferSize() {
        return (long) BUFFER_SIZE.get(segment());
    }

    public void setOperationCount(int count) {
        OPERATION_COUNT.set(segment(), count);
    }

    public int getOperationCount() {
        return (int) OPERATION_COUNT.get(segment());
    }

    public void setBenchmarkMode(Mode mode) {
        BENCHMARK_MODE.set(segment(), mode.value());
    }

    public Mode getBenchmarkMode() {
        return Mode.from((byte) BENCHMARK_MODE.get(segment()));
    }


}
