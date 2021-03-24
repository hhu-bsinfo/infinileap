package de.hhu.bsinfo.infinileap.example.benchmark.command;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.binding.NativeLogger;
import de.hhu.bsinfo.infinileap.example.benchmark.connection.BenchmarkServer;
import de.hhu.bsinfo.infinileap.example.benchmark.connection.ControlChannel;
import de.hhu.bsinfo.infinileap.example.benchmark.connection.ControlChannel.Action;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@CommandLine.Command(
        name = "server",
        description = "Runs the server part of the benchmark."
)
public class BenchmarkServerCommand implements Runnable {

    @CommandLine.Option(
            names = {"-l", "--listen"},
            description = "The server to connect to.")
    private InetSocketAddress listenAddress;

    @Override
    public void run() {
        NativeLogger.enable();

        // Establish control channel connection
        ControlChannel control;
        try {
            var controlAddress = new InetSocketAddress(listenAddress.getAddress(), listenAddress.getPort() - 1);
            log.info("Listening for control connection on {}", controlAddress);
            control = ControlChannel.listen(controlAddress);
            log.info("Accepted control connection");
        } catch (IOException e) {
            throw new RuntimeException("Establishing control channel failed", e);
        }

        try {
            // Loop until the client doesn't request more runs
            while (control.receiveAction() == Action.START_RUN) {

                // Create thread pool for server threads
                var threadCount = control.receiveThreadCount();
                var executorService = Executors.newFixedThreadPool(threadCount);
                log.info("Initializing run with {} threads", threadCount);

                // Collect all connections
                var servers = new BenchmarkServer[threadCount];
                for (int i = 0; i < threadCount; i++) {
                    try {
                        var serverAddress = new InetSocketAddress(listenAddress.getAddress(), listenAddress.getPort() + i);
                        servers[i] = BenchmarkServer.create(serverAddress);
                    } catch (ControlException e) {
                        log.error("Initializing servers failed", e);
                        return;
                    }
                }

                // Submit all server tasks to separate threads
                for (int i = 0; i < threadCount; i++) {
                    executorService.submit(servers[i]::start);
                }

                Action action;
                if ((action = control.receiveAction()) != Action.FINISH_RUN) {
                    throw new RuntimeException("Received unexpected action " + action.name());
                }

                // Shutdown all servers
                log.info("Shutting down servers");
                for (var server : servers) {
                    server.shutdown();
                }

                // Wait one second for all servers to shutdown
                LockSupport.parkNanos(Duration.ofSeconds(1L).toNanos());

                // Make sure that all servers terminated
                for (var server : servers) {
                    while (server.isRunning()) {
                        LockSupport.parkNanos(Duration.ofSeconds(1L).toNanos());
                        log.warn("Server on thread {} is still running", server.getWorkerThread().getName());
                    }
                }

                // Wait until thread pool terminates
                log.info("Shutting down thread pool");
                executorService.shutdown();
                if (!executorService.awaitTermination(5L, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Shutting down thread pool failed");
                }

                log.info("Benchmark run ended");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Awaiting thread pool termination failed", e);
        } catch (IOException e) {
            throw new RuntimeException("Control channel failed", e);
        }
    }
}
