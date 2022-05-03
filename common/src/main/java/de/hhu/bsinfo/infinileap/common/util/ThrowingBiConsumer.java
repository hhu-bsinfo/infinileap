package de.hhu.bsinfo.infinileap.common.util;

public interface ThrowingBiConsumer<S, T> {
    void accept(S first, T second) throws Exception;
}
