package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.struct.Struct;
import java.nio.ByteBuffer;
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
        var result = new Result();
        Verbs.postSendWorkRequest(handle, sendWorkRequest.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Posting send work request failed");
        }
    }

    public void post(final ReceiveWorkRequest receiveWorkRequest) {
        var result = new Result();
        Verbs.postReceiveWorkRequest(handle, receiveWorkRequest.getHandle(), result.getHandle());
        if (result.isError()) {
            LOGGER.error("Posting send work request failed");
        }
    }
}
