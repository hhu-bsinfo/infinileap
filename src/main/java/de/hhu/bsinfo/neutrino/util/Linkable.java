package de.hhu.bsinfo.neutrino.util;

public interface Linkable<T> {
    void linkWith(T other);
    void unlink();
}
