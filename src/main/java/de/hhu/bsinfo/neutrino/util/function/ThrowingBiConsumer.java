package de.hhu.bsinfo.neutrino.util.function;

public interface ThrowingBiConsumer<S, T> {
    void accept(S first, T second) throws Exception;
}
