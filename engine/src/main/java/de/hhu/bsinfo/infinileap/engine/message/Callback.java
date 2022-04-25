package de.hhu.bsinfo.infinileap.engine.message;

public interface Callback<T> {

    void onNext(T message);

    void onError(Throwable throwable);

    void onComplete();
}
