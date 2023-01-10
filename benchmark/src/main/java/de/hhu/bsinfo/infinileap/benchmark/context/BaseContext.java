package de.hhu.bsinfo.infinileap.benchmark.context;

import de.hhu.bsinfo.infinileap.benchmark.connection.BenchmarkCoordinator;
import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.benchmark.connection.BenchmarkClient;
import de.hhu.bsinfo.infinileap.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.benchmark.message.BenchmarkInstruction;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.ThreadParams;

import java.net.InetSocketAddress;

@State(Scope.Thread)
public abstract class BaseContext {

    protected BenchmarkClient connection;

    @Param({ "127.0.0.1" })
    public String serverAddress;

    @Param({ "2998" })
    public int serverPort;

    @Param({ "3998" })
    public int controlPort;

    @Setup(Level.Trial)
    public void setup(ThreadParams threadParams) throws ControlException, InterruptedException {

        // Establish coordinator connection
        var coordinator = BenchmarkCoordinator.connect(
                new InetSocketAddress(serverAddress, controlPort + threadParams.getThreadIndex())
        );

        coordinator.start();

        // Establish OpenUCX Connection
        connection = BenchmarkClient.connect(
                new InetSocketAddress(serverAddress, serverPort + threadParams.getThreadIndex())
        );

        // Fill in benchmark details
        var details = new BenchmarkDetails();
        fillDetails(details);

        coordinator.send(details);

        // Prepare connection using details
        connection.prepare(getInitialInstruction(), details);
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
