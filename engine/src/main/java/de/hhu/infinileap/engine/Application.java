package de.hhu.infinileap.engine;


import de.hhu.infinileap.engine.command.Loop;
import de.hhu.infinileap.engine.command.util.Constants;
import de.hhu.infinileap.engine.command.util.InetSocketAddressConverter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.InetSocketAddress;

@Slf4j
@CommandLine.Command(
        name = "engine",
        mixinStandardHelpOptions = true,
        subcommands = { Loop.class }
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
