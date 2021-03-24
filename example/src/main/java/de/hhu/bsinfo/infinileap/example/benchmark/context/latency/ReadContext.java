package de.hhu.bsinfo.infinileap.example.benchmark.context.latency;

import de.hhu.bsinfo.infinileap.example.benchmark.context.BaseContext;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
public class ReadContext extends BaseContext {

    @Param({ "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096" })
    public int bufferSize;

    @Override
    protected OpCode getInitialInstruction() {
        return OpCode.RUN_READ;
    }

    @Override
    protected void fillDetails(BenchmarkDetails details) {
        details.setBufferSize(bufferSize);
        details.setOperationCount(1);
        details.setBenchmarkMode(BenchmarkDetails.Mode.LATENCY);
    }

    public final void get() {
        connection.getLatency();
    }
}
