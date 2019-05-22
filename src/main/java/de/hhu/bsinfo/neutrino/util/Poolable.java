package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.verbs.Verbs;

public interface Poolable {

    default void releaseInstance() {
        Verbs.returnPoolableInstance(this);
    }
}
