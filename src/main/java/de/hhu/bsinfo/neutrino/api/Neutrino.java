package de.hhu.bsinfo.neutrino.api;

import de.hhu.bsinfo.neutrino.api.connection.ConnectionManager;
import de.hhu.bsinfo.neutrino.verbs.Context;
import de.hhu.bsinfo.neutrino.verbs.DeviceAttributes;
import de.hhu.bsinfo.neutrino.verbs.Port;
import de.hhu.bsinfo.neutrino.verbs.ProtectionDomain;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Neutrino {

    private static final Logger LOGGER = LoggerFactory.getLogger(Neutrino.class);

    private final Context context;
    private final Port port;
    private final ProtectionDomain protectionDomain;

    private Neutrino(final Context context, final Port port, final ProtectionDomain protectionDomain) {
        this.context = context;
        this.port = port;
        this.protectionDomain = protectionDomain;
    }

    @Nullable
    public static Neutrino newInstance() {
        var numDevices = DeviceAttributes.getDeviceCount();

        if(numDevices <= 0) {
            LOGGER.error("No RDMA devices were found in your system");
            return null;
        }

        var context = Context.openDevice();
        if (context == null) {
            LOGGER.error("Opening device context failed");
            return null;
        }

        LOGGER.info("Opened device context for {}", context.getDeviceName());

        var port = context.queryPort();
        if (port == null) {
            LOGGER.error("Querying port failed");
            return null;
        }

        var protectionDomain = context.allocateProtectionDomain();
        if (protectionDomain == null) {
            LOGGER.error("Allocating protection domain failed");
            return null;
        }

        var neutrino = new Neutrino(context, port, protectionDomain);
    }

    public static void printBanner() {
        InputStream inputStream = Neutrino.class.getClassLoader().getResourceAsStream("banner.txt");

        if (inputStream == null) {
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String banner = reader.lines().collect(Collectors.joining(System.lineSeparator()));

        System.out.print("\n");
        System.out.printf(banner, BuildConfig.VERSION, BuildConfig.BUILD_DATE, BuildConfig.GIT_BRANCH, BuildConfig.GIT_COMMIT);
        System.out.print("\n\n");
    }
}
