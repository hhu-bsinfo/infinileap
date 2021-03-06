package de.hhu.bsinfo.infinileap.example.benchmark.command;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.binding.NativeLogger;
import de.hhu.bsinfo.infinileap.example.benchmark.connection.BenchmarkServer;
import de.hhu.bsinfo.infinileap.primitive.NativeLong;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import picocli.CommandLine;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@CommandLine.Command(
        name = "server",
        description = "Runs the server part of the benchmark."
)
public class BenchmarkServerCommand implements Runnable {

    private static final int DEFAULT_THREAD_COUNT = 1;

    @CommandLine.Option(
            names = {"-l", "--listen"},
            description = "The server to connect to.")
    private InetSocketAddress listenAddress;

    @CommandLine.Option(
            names = {"-t", "--threads"},
            description = "The server to connect to.")
    private int threads = DEFAULT_THREAD_COUNT;

    private ExecutorService executorService;

    @Override
    public void run() {
        NativeLogger.enable();
        executorService = Executors.newFixedThreadPool(threads);

        // Collect all connections
        var servers = new BenchmarkServer[threads];
        for (int i = 0; i < threads; i++) {
            try {
                var serverAddress = new InetSocketAddress(listenAddress.getAddress(), listenAddress.getPort() + i);
                servers[i] = BenchmarkServer.create(serverAddress);
            } catch (ControlException e) {
                log.error("Initializing servers failed", e);
                return;
            }
        }

        // Submit all server tasks to separate threads
        var futures = new Future<?>[threads];
        for (int i = 0; i < threads; i++) {
            futures[i] = executorService.submit(servers[i]::start);
        }

        // Wait on all servers to complete
        for (var future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Waiting on server completion failed");
                return;
            }
        }

    }
}
