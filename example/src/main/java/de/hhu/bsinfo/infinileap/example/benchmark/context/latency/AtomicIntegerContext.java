package de.hhu.bsinfo.infinileap.example.benchmark.context.latency;

import de.hhu.bsinfo.infinileap.example.benchmark.context.BaseContext;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
public class AtomicIntegerContext extends BaseContext {

    @Param({ "4" })
    public int bufferSize;

    @Override
    protected OpCode getInitialInstruction() {
        return OpCode.RUN_ATOMIC;
    }

    @Override
    protected void fillDetails(BenchmarkDetails details) {
        details.setBufferSize(bufferSize);
        details.setOperationCount(1);
        details.setBenchmarkMode(BenchmarkDetails.Mode.LATENCY);
    }

    public final void atomicAdd() {
        connection.add32();
    }

    public final void atomicSwap() {
        connection.swap32();
    }

    public final void atomicCompareAndSwap() {
        connection.compareAndSwap32();
    }

    public final void atomicAnd() {
        connection.and32();
    }

    public final void atomicOr() {
        connection.or32();
    }

    public final void atomicXor() {
        connection.xor32();
    }
}
