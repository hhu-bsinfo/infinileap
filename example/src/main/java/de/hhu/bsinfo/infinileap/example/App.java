package de.hhu.bsinfo.infinileap.example;

import de.hhu.bsinfo.infinileap.example.command.Root;
import de.hhu.bsinfo.infinileap.example.util.InetSocketAddressConverter;
import picocli.CommandLine;

import java.net.InetSocketAddress;

public final class App {

    @SuppressWarnings("CallToSystemExit")
    public static void main(String... args) {
        var exitCode = new CommandLine(new Root())
                .registerConverter(InetSocketAddress.class, new InetSocketAddressConverter(22222))
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);

        System.exit(exitCode);
    }
}
