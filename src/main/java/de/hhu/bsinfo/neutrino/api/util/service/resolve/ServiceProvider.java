package de.hhu.bsinfo.neutrino.api.util.service.resolve;

import de.hhu.bsinfo.neutrino.api.util.service.Service;

@FunctionalInterface
public interface ServiceProvider {
    <T extends Service<?>> T get(final Class<T> module);
}
