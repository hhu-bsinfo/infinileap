package de.hhu.bsinfo.infinileap.example.benchmark.context;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.ThreadParams;

@State(Scope.Thread)
public class MessagingContext extends BaseContext {

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
        return OpCode.RUN_SEND_LATENCY;
    }

    @Override
    protected void fillDetails(BenchmarkDetails details) {
        details.setBufferSize(bufferSize);
    }

    public final void blockingSendTagged() {
        connection.blockingSendTagged();
    }
}
