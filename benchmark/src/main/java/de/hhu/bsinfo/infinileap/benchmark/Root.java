package de.hhu.bsinfo.infinileap.benchmark;

import de.hhu.bsinfo.infinileap.benchmark.command.BenchmarkClientCommand;
import de.hhu.bsinfo.infinileap.benchmark.command.BenchmarkServerCommand;
import de.hhu.bsinfo.infinileap.benchmark.command.BenchmarkUtilCommand;
import picocli.CommandLine;

@CommandLine.Command(
    name = "benchmark",
    description = "",
    subcommands = {
        // Benchmark Commands
        BenchmarkClientCommand.class, BenchmarkServerCommand.class, BenchmarkUtilCommand.class
    }
)
public class Root implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}