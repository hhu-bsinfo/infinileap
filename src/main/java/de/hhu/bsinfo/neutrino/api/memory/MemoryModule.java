package de.hhu.bsinfo.neutrino.api.memory;

import de.hhu.bsinfo.neutrino.api.core.CoreModule;
import de.hhu.bsinfo.neutrino.api.module.Module;
import de.hhu.bsinfo.neutrino.api.module.ModuleOptions;
import de.hhu.bsinfo.neutrino.api.util.NullOptions;

import javax.inject.Inject;

public class MemoryModule extends Module<NullOptions> {

    @Inject
    private CoreModule core;

    @Override
    protected void onInit() {

    }

    @Override
    protected void onShutdown() {

    }
}
