package de.hhu.bsinfo.neutrino.util;

public interface ObjectPool<T> {
    T getInstance();
    void returnInstance(T object);
}
