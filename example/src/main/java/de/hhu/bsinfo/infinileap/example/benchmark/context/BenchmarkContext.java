package de.hhu.bsinfo.infinileap.example.benchmark.context;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.binding.Endpoint;
import de.hhu.bsinfo.infinileap.example.benchmark.connection.BenchmarkConnection;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.util.BenchmarkType;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.net.InetSocketAddress;

@State(Scope.Benchmark)
public class BenchmarkContext {

    private BenchmarkConnection connection;

    @Param({ "127.0.0.1" })
    public String serverAddress;

    @Param({ "2998" })
    public int serverPort;

    @Param({ "128", "256", "512", "1024", "2048", "4096" })
    public int bufferSize;

    @Setup(Level.Trial)
    public void setup(BenchmarkParams params) throws ControlException {

        var address = new InetSocketAddress(serverAddress, serverPort);

        connection = BenchmarkConnection.establish(address);
        try (var details = new BenchmarkDetails()) {
            details.setBufferSize(bufferSize);
            connection.prepare(BenchmarkType.RDMA_THROUGHPUT, details);
        }
    }

    @TearDown(Level.Trial)
    public void cleanup() {
        connection.synchronize();
        connection.close();
        connection = null;
    }

    public final void blockingGet() {
        connection.blockingGet();
    }

    public final void blockingPut() {
        connection.blockingPut();
    }

}
