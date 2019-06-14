package de.hhu.bsinfo.neutrino.api.util.service.inject;

import de.hhu.bsinfo.neutrino.api.util.FormattedRuntimeException;

public class InjectionException extends FormattedRuntimeException {

    public InjectionException(String format, Object... args) {
        super(format, args);
    }

    public InjectionException(String format, Throwable cause, Object... args) {
        super(format, cause, args);
    }

    public InjectionException(String format, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... args) {
        super(format, cause, enableSuppression, writableStackTrace, args);
    }
}
