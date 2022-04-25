package de.hhu.bsinfo.infinileap.benchmark.context.latency;

import de.hhu.bsinfo.infinileap.benchmark.context.BaseContext;
import de.hhu.bsinfo.infinileap.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.benchmark.message.BenchmarkInstruction.OpCode;
import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
public class AtomicLongContext extends BaseContext {

    @Param({ "8" })
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
        connection.add64();
    }

    public final void atomicSwap() {
        connection.swap64();
    }

    public final void atomicCompareAndSwap() {
        connection.compareAndSwap64();
    }

    public final void atomicAnd() {
        connection.and64();
    }

    public final void atomicOr() {
        connection.or64();
    }

    public final void atomicXor() {
        connection.xor64();
    }
}
