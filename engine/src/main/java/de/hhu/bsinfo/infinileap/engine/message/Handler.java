package de.hhu.bsinfo.infinileap.engine.message;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {
    int identifier();
}
