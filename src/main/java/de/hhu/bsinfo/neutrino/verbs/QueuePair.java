package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueuePair implements NativeObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueuePair.class);

    private final long handle;

    protected QueuePair(long handle) {
        this.handle = handle;
    }

    @Override
    public long getHandle() {
        return handle;
    }

    public void post(final SendWorkRequest sendWorkRequest) {
        var result = Verbs.getResultPool().getInstance();

        Verbs.postSendWorkRequest(handle, sendWorkRequest.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Posting send work request failed");
        }

        Verbs.getResultPool().returnInstance(result);
    }

    public void post(final ReceiveWorkRequest receiveWorkRequest) {
        var result = Verbs.getResultPool().getInstance();

        Verbs.postReceiveWorkRequest(handle, receiveWorkRequest.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Posting send work request failed");
        }

        Verbs.getResultPool().returnInstance(result);
    }
}
