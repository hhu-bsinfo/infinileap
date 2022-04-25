package de.hhu.bsinfo.infinileap.benchmark.command;

import de.hhu.bsinfo.infinileap.benchmark.connection.ControlChannel;
import de.hhu.bsinfo.infinileap.benchmark.connection.ControlChannel.Action;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import picocli.CommandLine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@CommandLine.Command(
        name = "client",
        description = "Runs the client part of the benchmark."
)
public class BenchmarkClientCommand implements Runnable {

    private static final int[] DEFAULT_THREAD_COUNT = { 1 };

    private static final String DEFAULT_PREFIX = "benchmark";

    private static final int BUFFER_SIZE_ALl = -1;

    private static DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("uuuuMMddHHmmss")
            .withZone(ZoneId.of("UTC"));

    @CommandLine.Option(
            names = {"-c", "--connect"},
            description = "The server to connect to")
    private InetSocketAddress serverAddress;

    @CommandLine.Option(
            names = "--include",
            split = ",",
            description = "The names of benchmarks to execute")
    private List<String> includes = Collections.emptyList();

    @CommandLine.Option(
            names = {"-t", "--threads"},
            split = ",",
            description = "The server to connect to")
    private int[] threads = DEFAULT_THREAD_COUNT;

    @CommandLine.Option(
            names = {"-s", "--size"},
            description = "The buffer size")
    private int bufferSize = BUFFER_SIZE_ALl;

    @CommandLine.Option(
            names = {"--short"},
            description = "Performs a short test run")
    private boolean isShortRun;

    @CommandLine.Option(
            names = {"-p", "--prefix"},
            description = "Prefix used for output file(s)")
    private String prefix = DEFAULT_PREFIX;

    @CommandLine.Option(
            names = {"-o", "--operations"},
            description = "Number of operations per benchmark method invocation")
    private int operations = 1;

    private ControlChannel control;

    @Override
    public void run() {

        // Establish control channel connection
        try {
            var controlAddress = new InetSocketAddress(serverAddress.getAddress(), serverAddress.getPort() - 1);
            log.info("Connecting with control server at {}", controlAddress);
            control = ControlChannel.connect(controlAddress);
            log.info("Connection established");
        } catch (IOException e) {
            throw new RuntimeException("Establishing control channel failed", e);
        }

        try {

            // Iterate through all thread counts
            for (var threadCount : threads) {

                // Configure server threads
                control.sendAction(Action.START_RUN);
                control.sendThreadCount(threadCount);

                var outputFile = String.format("%s_%s_%d-threads.csv",
                        prefix, FORMATTER.format(Instant.now()), threadCount);

                // Create base options
                var options = new OptionsBuilder()
                        .resultFormat(ResultFormatType.CSV)
                        .result(outputFile)
                        .param("serverAddress", serverAddress.getHostString())
                        .param("serverPort", String.valueOf(serverAddress.getPort()))
                        .param("operationCount", String.valueOf(operations))
                        .operationsPerInvocation(operations)
                        .threads(threadCount)
                        .detectJvmArgs();

                if (isShortRun) {
                    options = options
                            .measurementIterations(1)
                            .measurementTime(TimeValue.milliseconds(1))
                            .warmupIterations(1)
                            .warmupTime(TimeValue.milliseconds(1));
                }

                if (bufferSize != BUFFER_SIZE_ALl) {
                    options = options.param("bufferSize", String.valueOf(bufferSize));
                }

                // Include the specified benchmarks
                for (var benchmark : includes) {
                    options = options.include(benchmark);
                }

                try {
                    new Runner(options.build()).run();
                } catch (RunnerException e) {
                    log.error("Running benchmark(s) failed", e);
                }

                // Signal that the current run has ended
                control.sendAction(Action.FINISH_RUN);

                // Wait for server to kill all threads
                LockSupport.parkNanos(Duration.ofSeconds(5).toNanos());
            }

            // Signal that all runs have ended
            control.sendAction(Action.SHUTDOWN);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
