package de.hhu.bsinfo.neutrino.api.util;

import org.slf4j.helpers.MessageFormatter;

public class FormattedRuntimeException extends RuntimeException {

    public FormattedRuntimeException(String format, Object... args) {
        super(MessageFormatter.arrayFormat(format, args).getMessage());
    }

    public FormattedRuntimeException(String format, Throwable cause, Object... args) {
        super(MessageFormatter.arrayFormat(format, args).getMessage(), cause);
    }

    public FormattedRuntimeException(String format, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... args) {
        super(MessageFormatter.arrayFormat(format, args).getMessage(), cause, enableSuppression, writableStackTrace);
    }
}
