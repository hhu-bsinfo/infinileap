package de.hhu.bsinfo.neutrino.api.connection;

import de.hhu.bsinfo.neutrino.api.core.CoreModule;
import de.hhu.bsinfo.neutrino.api.module.Module;
import de.hhu.bsinfo.neutrino.api.util.NullOptions;

import javax.inject.Inject;

public class ConnectionModule extends Module<NullOptions> {

    @Inject
    private CoreModule core;

    @Override
    protected void onInit() {

    }

    @Override
    protected void onShutdown() {

    }
}
