package de.hhu.bsinfo.infinileap.example.benchmark.context;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import de.hhu.bsinfo.infinileap.example.util.BenchmarkType;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.ThreadParams;

@State(Scope.Thread)
public class WriteContext extends BaseContext {

    @Param({ "16", "32", "64", "128", "256", "512", "1024", "2048", "4096" })
    public int bufferSize;

    @Setup(Level.Trial)
    public void setup(ThreadParams threadParams) throws ControlException {
        setupBenchmark(threadParams);
    }

    @TearDown(Level.Trial)
    public void cleanup() {
        cleanupBenchmark();
    }

    @Override
    protected OpCode getInitialInstruction() {
        return OpCode.RUN_WRITE_LATENCY;
    }

    @Override
    protected void fillDetails(BenchmarkDetails details) {
        details.setBufferSize(bufferSize);
    }

    public final void blockingPut() {
        connection.blockingPut();
    }
}
