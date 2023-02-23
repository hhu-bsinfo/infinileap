package de.hhu.bsinfo.infinileap.sidecar;

import de.hhu.bsinfo.infinileap.common.util.InetSocketAddressConverter;
import de.hhu.bsinfo.infinileap.sidecar.command.Start;
import picocli.CommandLine;

import java.net.InetSocketAddress;

@CommandLine.Command(
        name = "benchmark",
        description = "",
        subcommands = {
                Start.class
        }
)
public final class App implements Runnable {

    private static final InetSocketAddressConverter ADDRESS_CONVERTER = new InetSocketAddressConverter(2998);

    @SuppressWarnings("CallToSystemExit")
    public static void main(String... args) {
        var app = new App();
        var exitCode = new CommandLine(app)
                .registerConverter(InetSocketAddress.class, ADDRESS_CONVERTER::convert)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);

        System.exit(exitCode);
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
