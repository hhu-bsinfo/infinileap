package de.hhu.infinileap.engine.message;

public interface Callback<T> {

    void onNext(T message);

    void onError(Throwable throwable);

    void onComplete();
}
