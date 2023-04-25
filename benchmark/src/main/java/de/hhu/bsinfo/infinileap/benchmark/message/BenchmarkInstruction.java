package de.hhu.bsinfo.infinileap.benchmark.message;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

import java.lang.invoke.VarHandle;
import java.util.Arrays;

public class BenchmarkInstruction extends NativeObject {

    public enum OpCode {
        // Benchmarks
        RUN_READ(0x01),
        RUN_WRITE(0x02),
        RUN_SEND(0x03),
        RUN_PINGPONG(0x04),
        RUN_ATOMIC(0x05),

        // Commands
        RELEASE_BARRIER(0x20),
        FINISH(0x21);

        private final int value;

        OpCode(int value) {
            this.value = value;
        }

        int value() {
            return value;
        }

        static OpCode from(int value) {
            return Arrays.stream(values())
                    .filter(it -> it.value() == value)
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }

    private static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("op_code")
    );

    private static final VarHandle OP_CODE =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("op_code"));

    public BenchmarkInstruction() {
        this(SegmentScope.auto());
    }

    public BenchmarkInstruction(SegmentScope scope) {
        super(MemorySegment.allocateNative(LAYOUT, scope));
    }

    public BenchmarkInstruction(OpCode opCode) {
        this(opCode, SegmentScope.auto());
    }

    public BenchmarkInstruction(OpCode operation, SegmentScope scope) {
        super(MemorySegment.allocateNative(LAYOUT, scope));
        setOperation(operation);
    }

    private void setOperation(OpCode operation) {
        OP_CODE.set(segment(), operation.value());
    }

    public OpCode opCode() {
        return OpCode.from((int) OP_CODE.get(segment()));
    }
}
