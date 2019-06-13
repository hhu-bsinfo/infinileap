package de.hhu.bsinfo.neutrino.api.connection;

import de.hhu.bsinfo.neutrino.buffer.RegisteredBuffer;
import de.hhu.bsinfo.neutrino.verbs.QueuePair;

public class Connection {

    private final QueuePair queuePair;
    private final RegisteredBuffer receiveBuffer;

    public Connection(QueuePair queuePair, RegisteredBuffer receiveBuffer) {
        this.queuePair = queuePair;
        this.receiveBuffer = receiveBuffer;
    }
}
