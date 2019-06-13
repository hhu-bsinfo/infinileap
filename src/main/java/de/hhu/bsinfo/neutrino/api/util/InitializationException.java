package de.hhu.bsinfo.neutrino.api.util;

public class InitializationException extends FormattedRuntimeException {

    public InitializationException(String format, Object... args) {
        super(format, args);
    }

    public InitializationException(String format, Throwable cause, Object... args) {
        super(format, cause, args);
    }

    public InitializationException(String format, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... args) {
        super(format, cause, enableSuppression, writableStackTrace, args);
    }
}
