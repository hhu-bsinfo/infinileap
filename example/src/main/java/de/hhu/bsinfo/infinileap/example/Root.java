package de.hhu.bsinfo.infinileap.example;

import de.hhu.bsinfo.infinileap.example.demo.*;
import de.hhu.bsinfo.infinileap.example.demo.engine.Engine;
import picocli.CommandLine;

@CommandLine.Command(
    name = "infinileap",
    description = "",
    subcommands = {
        // Demos
        Messaging.class, Memory.class, Atomic.class, Streaming.class, ActiveMessage.class, Info.class,

        // Engine
        Engine.class
    }
)
public class Root implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}