package de.hhu.bsinfo.infinileap.example.benchmark.command;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(
        name = "benchmark",
        description = "A suite consisting of various benchmarks.",
        subcommands = { BenchmarkClientCommand.class, BenchmarkServerCommand.class }
)
public class BenchmarkCommand {}
