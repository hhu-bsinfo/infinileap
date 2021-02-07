package de.hhu.bsinfo.infinileap.example.command;

import picocli.CommandLine;

@CommandLine.Command(
    name = "infinileap",
    description = "",
    subcommands = {
        Messaging.class, Memory.class, Atomic.class, Streaming.class, Info.class
    }
)
public class Root implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}