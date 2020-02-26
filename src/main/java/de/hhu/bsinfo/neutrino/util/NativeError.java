package de.hhu.bsinfo.neutrino.util;

public class NativeError extends RuntimeException {

    public NativeError() {}

    public NativeError(String message) {
        super(message);
    }

    public NativeError(String message, Throwable cause) {
        super(message, cause);
    }

    public NativeError(Throwable cause) {
        super(cause);
    }

    protected NativeError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
