package de.hhu.bsinfo.infinileap.engine.event.loop;

import de.hhu.bsinfo.infinileap.engine.event.loop.AbstractEventLoop;

public interface EventLoopFactory<T extends AbstractEventLoop> {

    /**
     * Creates a new event loop instance.
     */
    T newInstance();
}
