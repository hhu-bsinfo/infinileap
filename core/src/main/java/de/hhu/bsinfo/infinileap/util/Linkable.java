package de.hhu.bsinfo.infinileap.util;

public interface Linkable<T> {
    void linkWith(T other);
    void unlink();
}
