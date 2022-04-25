package de.hhu.bsinfo.infinileap.example;

import de.hhu.bsinfo.infinileap.common.util.InetSocketAddressConverter;
import picocli.CommandLine;

import java.net.InetSocketAddress;

public final class App {

    private static final InetSocketAddressConverter ADDRESS_CONVERTER = new InetSocketAddressConverter(2998);

    @SuppressWarnings("CallToSystemExit")
    public static void main(String... args) {
        var exitCode = new CommandLine(new Root())
                .registerConverter(InetSocketAddress.class, ADDRESS_CONVERTER::convert)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);

        System.exit(exitCode);
    }
}
