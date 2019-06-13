package de.hhu.bsinfo.neutrino.api;

import de.hhu.bsinfo.neutrino.api.connection.ConnectionModule;
import de.hhu.bsinfo.neutrino.api.message.MessageModule;
import de.hhu.bsinfo.neutrino.api.module.Module;
import de.hhu.bsinfo.neutrino.api.module.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public final class Neutrino {

    private static final Logger LOGGER = LoggerFactory.getLogger(Neutrino.class);

    private final ModuleManager moduleManager = new ModuleManager();

    private Neutrino() {}

    public static Neutrino newInstance() {
        var neutrino = new Neutrino();
        neutrino.initialize();
        return neutrino;
    }

    private void initialize() {
        registerModules();
        initializeModules();
    }

    private void registerModules() {
        moduleManager.register(ConnectionModule.class);
        moduleManager.register(MessageModule.class);
    }

    private void initializeModules() {
        moduleManager.initialize();
    }

    public <T extends Module<?>> T getModule(final Class<T> module) {
        return moduleManager.get(module);
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
