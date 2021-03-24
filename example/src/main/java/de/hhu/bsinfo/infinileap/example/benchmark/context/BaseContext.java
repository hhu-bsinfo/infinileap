package de.hhu.bsinfo.infinileap.example.benchmark.context;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.example.benchmark.connection.BenchmarkClient;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.example.benchmark.message.BenchmarkInstruction;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.infra.ThreadParams;

import java.net.InetSocketAddress;

@State(Scope.Thread)
public abstract class BaseContext {

    protected BenchmarkClient connection;

    @Param({ "127.0.0.1" })
    public String serverAddress;

    @Param({ "2998" })
    public int serverPort;

    @Setup(Level.Trial)
    public void setup(ThreadParams threadParams) throws ControlException, InterruptedException {
        var address = new InetSocketAddress(serverAddress, serverPort + threadParams.getThreadIndex());

        connection = BenchmarkClient.connect(address);
        try (var details = new BenchmarkDetails()) {
            fillDetails(details);
            connection.prepare(getInitialInstruction(), details);
        }
    }

    @TearDown(Level.Trial)
    public void cleanup() throws InterruptedException {
        connection.synchronize();
        connection.close();
        connection = null;
    }

    protected abstract BenchmarkInstruction.OpCode getInitialInstruction();

    protected abstract void fillDetails(BenchmarkDetails details);
}
