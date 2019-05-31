package de.hhu.bsinfo.neutrino.util;

@FunctionalInterface
public interface IndexedConsumer<T> {
    void accept(int index, T target);
}
