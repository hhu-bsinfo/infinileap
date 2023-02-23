package de.hhu.bsinfo.infinileap.sidecar.command;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.InetSocketAddress;

@Slf4j
@CommandLine.Command(
        name = "start",
        description = "Starts the sidecar."
)
public class Start implements Runnable {

    @CommandLine.Option(
            names = {"--bind"},
            description = "The local ip address the sidecar should bind to.")
    private InetSocketAddress bindAddress;

    @Override
    public void run() {



    }
}
