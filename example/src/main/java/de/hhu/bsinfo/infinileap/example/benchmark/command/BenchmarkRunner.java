package de.hhu.bsinfo.infinileap.example.benchmark.command;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import picocli.CommandLine;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

@Slf4j
@CommandLine.Command(
        name = "run",
        description = "Runs a set of benchmarks."
)
public class BenchmarkRunner implements Runnable {

    @CommandLine.Option(
            names = {"-c", "--connect"},
            description = "The server to connect to.")
    private InetSocketAddress serverAddress;

    @CommandLine.Option(
            names = "--include",
            description = "The names of benchmarks to execute.")
    private List<String> includes = Collections.emptyList();

    @Override
    public void run() {

        // Create base options
        var options = new OptionsBuilder()
                .resultFormat(ResultFormatType.CSV)
                .param("serverAddress", serverAddress.getHostString())
                .param("serverPort", String.valueOf(serverAddress.getPort()))
                .detectJvmArgs();

        // Include the specified benchmarks
        for (var benchmark : includes) {
            options = options.include(benchmark);
        }

        try {
            new Runner(options.build()).run();
        } catch (RunnerException e) {
            log.error("Running benchmark(s) failed", e);
        }
    }
}
