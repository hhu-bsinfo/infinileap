package de.hhu.bsinfo.infinileap.example.benchmark.context.throughput;

import de.hhu.bsinfo.infinileap.example.benchmark.ThroughputBenchmark;
import de.hhu.bsinfo.infinileap.example.benchmark.context.BaseContext;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction.OpCode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class ReadContext extends BaseContext {

    @Param({ "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096" })
    public int bufferSize;

    @Param({ ThroughputBenchmark.DEFAULT_OPERATION_COUNT_PARAM })
    public int operationCount;

    @Override
    protected OpCode getInitialInstruction() {
        return OpCode.RUN_READ;
    }

    @Override
    protected void fillDetails(BenchmarkDetails details) {
        details.setBufferSize(bufferSize);
        details.setOperationCount(operationCount);
        details.setBenchmarkMode(BenchmarkDetails.Mode.THROUGHPUT);
    }

    public final void read() {
        connection.getThroughput();
    }
}
