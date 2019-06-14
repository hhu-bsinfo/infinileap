package de.hhu.bsinfo.neutrino.api.util.service;

import de.hhu.bsinfo.neutrino.api.util.FormattedRuntimeException;

public class ModuleInstantiationException extends FormattedRuntimeException {

    public ModuleInstantiationException(String format, Object... args) {
        super(format, args);
    }

    public ModuleInstantiationException(String format, Throwable cause, Object... args) {
        super(format, cause, args);
    }

    public ModuleInstantiationException(String format, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... args) {
        super(format, cause, enableSuppression, writableStackTrace, args);
    }
}
