package de.hhu.bsinfo.infinileap.example.benchmark.message;

import de.hhu.bsinfo.infinileap.binding.NativeObject;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;

import java.lang.invoke.VarHandle;
import java.util.Arrays;

import static jdk.incubator.foreign.CLinker.*;

public class BenchmarkInstruction extends NativeObject {

    public enum OpCode {
        // Benchmarks
        RUN_READ_LATENCY(0x01),
        RUN_WRITE_LATENCY(0x02),

        RUN_SEND_LATENCY(0x03),
        RUN_PINGPONG_LATENCY(0x04),

        RUN_ATOMIC_LATENCY(0x05),

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

    private static final MemoryLayout LAYOUT = MemoryLayout.ofStruct(
            C_INT.withName("op_code")
    );

    private static final VarHandle OP_CODE =
            LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("op_code"));

    public BenchmarkInstruction() {
        super(MemorySegment.allocateNative(LAYOUT));
    }
    public BenchmarkInstruction(OpCode operation) {
        super(MemorySegment.allocateNative(LAYOUT));
        setOperation(operation);
    }

    private void setOperation(OpCode operation) {
        OP_CODE.set(segment(), operation.value());
    }

    public OpCode opCode() {
        return OpCode.from((int) OP_CODE.get(segment()));
    }
}
