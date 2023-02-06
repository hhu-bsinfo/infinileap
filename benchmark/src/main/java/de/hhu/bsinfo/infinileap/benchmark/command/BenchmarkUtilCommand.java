package de.hhu.bsinfo.infinileap.benchmark.command;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import picocli.CommandLine;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@CommandLine.Command(
        name = "util",
        description = "Runs benchmarks written for utilities."
)
public class BenchmarkUtilCommand implements Runnable {

    @CommandLine.Option(
            names = "--include",
            split = ",",
            description = "The names of benchmarks to execute")
    private List<String> includes = Collections.emptyList();

    private static final String DEFAULT_PREFIX = "benchmark";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("uuuuMMddHHmmss")
            .withZone(ZoneId.of("UTC"));

    @CommandLine.Option(
            names = {"-p", "--prefix"},
            description = "Prefix used for output file(s)")
    private String prefix = DEFAULT_PREFIX;


    @Override
    public void run() {

        var outputFile = String.format("%s_%s.csv",
                prefix, FORMATTER.format(Instant.now()));

        // Create base options
        var options = new OptionsBuilder()
                .resultFormat(ResultFormatType.CSV)
                .result(outputFile)
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
