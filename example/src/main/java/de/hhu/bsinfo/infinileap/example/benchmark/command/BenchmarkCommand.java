package de.hhu.bsinfo.infinileap.example.benchmark.command;

import de.hhu.bsinfo.infinileap.example.benchmark.connection.BenchmarkServer;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(
        name = "benchmark",
        description = "A suite consisting of various benchmarks.",
        subcommands = { BenchmarkRunner.class, BenchmarkServer.class }
)
public class BenchmarkCommand {}
