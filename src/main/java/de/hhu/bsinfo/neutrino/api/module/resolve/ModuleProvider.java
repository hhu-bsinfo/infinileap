package de.hhu.bsinfo.neutrino.api.module.resolve;

import de.hhu.bsinfo.neutrino.api.module.Module;
import de.hhu.bsinfo.neutrino.api.module.ModuleOptions;

import java.util.function.Supplier;

@FunctionalInterface
public interface ModuleProvider {
    <T extends Module<?>> T get(final Class<T> module);
}
