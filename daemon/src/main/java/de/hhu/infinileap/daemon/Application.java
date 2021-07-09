package de.hhu.infinileap.daemon;

import de.hhu.infinileap.daemon.command.Demo;
import de.hhu.infinileap.daemon.command.Start;
import de.hhu.infinileap.daemon.util.Constants;
import de.hhu.infinileap.daemon.util.InetSocketAddressConverter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.InetSocketAddress;

@Slf4j
@CommandLine.Command(
        name = "daemon",
        mixinStandardHelpOptions = true,
        version = Constants.DAEMON_VERSION,
        subcommands = { Start.class, Demo.class }
)
public final class Application {

    private Application() {}

    @SuppressWarnings({"CallToSystemExit", "InstantiationOfUtilityClass"})
    public static void main(String... args) {
        final var application = new Application();
        final var exitCode = new CommandLine(application)
                .registerConverter(InetSocketAddress.class, new InetSocketAddressConverter(Constants.DEFAULT_PORT))
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);

        System.exit(exitCode);
    }
}
