package de.hhu.bsinfo.neutrino.api.util.service.inject;

@FunctionalInterface
public interface Injector {
    void inject(Object target);
}
