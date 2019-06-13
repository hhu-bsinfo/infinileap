package de.hhu.bsinfo.neutrino.api.module.inject;

@FunctionalInterface
public interface Injector {
    void inject(Object target);
}
