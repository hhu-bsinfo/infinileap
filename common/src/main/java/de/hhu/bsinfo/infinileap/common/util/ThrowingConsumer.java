package de.hhu.bsinfo.infinileap.common.util;

public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T element) throws E;
}
