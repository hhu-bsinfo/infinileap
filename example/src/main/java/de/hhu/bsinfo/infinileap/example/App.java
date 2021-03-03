package de.hhu.bsinfo.infinileap.example;

import de.hhu.bsinfo.infinileap.example.util.Constants;
import de.hhu.bsinfo.infinileap.example.util.InetSocketAddressConverter;
import picocli.CommandLine;

import java.net.InetSocketAddress;

public final class App {

    @SuppressWarnings("CallToSystemExit")
    public static void main(String... args) {
        var exitCode = new CommandLine(new Root())
                .registerConverter(InetSocketAddress.class, new InetSocketAddressConverter(Constants.DEFAULT_PORT))
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);

        System.exit(exitCode);
    }
}
