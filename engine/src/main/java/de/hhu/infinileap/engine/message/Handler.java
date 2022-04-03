package de.hhu.infinileap.engine.message;
import de.hhu.bsinfo.infinileap.binding.Identifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {
    int identifier();
}
