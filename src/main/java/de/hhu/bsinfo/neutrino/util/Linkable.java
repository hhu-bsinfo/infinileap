package de.hhu.bsinfo.neutrino.util;

@FunctionalInterface
public interface Linkable<T> {
    void linkWith(T other);
}
