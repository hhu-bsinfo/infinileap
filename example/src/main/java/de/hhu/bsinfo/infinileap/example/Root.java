package de.hhu.bsinfo.infinileap.example;

import de.hhu.bsinfo.infinileap.example.benchmark.command.BenchmarkCommand;
import de.hhu.bsinfo.infinileap.example.demo.*;
import picocli.CommandLine;

@CommandLine.Command(
    name = "infinileap",
    description = "",
    subcommands = {
        // Demos
        Messaging.class, Memory.class, Atomic.class, Streaming.class, ActiveMessage.class, Info.class,

        // Benchmarks
        BenchmarkCommand.class
    }
)
public class Root implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}