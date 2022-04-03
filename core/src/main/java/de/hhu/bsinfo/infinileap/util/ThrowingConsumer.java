package de.hhu.bsinfo.infinileap.util;

public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T element) throws E;
}
