package de.hhu.bsinfo.infinileap.example.benchmark.context;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.example.benchmark.connection.BenchmarkClient;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.net.InetSocketAddress;

@State(Scope.Benchmark)
public abstract class BaseContext {

    protected BenchmarkClient connection;

    @Param({ "127.0.0.1" })
    public String serverAddress;

    @Param({ "2998" })
    public int serverPort;

    protected void setupBenchmark() throws ControlException {
        var address = new InetSocketAddress(serverAddress, serverPort);

        connection = BenchmarkClient.connect(address);
        try (var details = new BenchmarkDetails()) {
            fillDetails(details);
            connection.prepare(getInitialInstruction(), details);
        }
    }

    protected void cleanupBenchmark() {
        connection.synchronize();
        connection.close();
        connection = null;
    }

    protected abstract BenchmarkInstruction.OpCode getInitialInstruction();

    protected abstract void fillDetails(BenchmarkDetails details);
}
