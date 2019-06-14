package de.hhu.bsinfo.neutrino.api.message;

import de.hhu.bsinfo.neutrino.api.util.NullOptions;
import de.hhu.bsinfo.neutrino.api.util.service.Service;

public abstract class MessageService extends Service<NullOptions> {

    public abstract void testMethod();
}
