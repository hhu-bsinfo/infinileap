package de.hhu.bsinfo.neutrino.api.connection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.StringJoiner;

public class Connection {

    private short localId;
    private int queuePairNumber;

    public Connection() {}

    public Connection(short localId, int queuePairNumber) {
        this.localId = localId;
        this.queuePairNumber = queuePairNumber;
    }

    public Connection(ByteBuffer buffer) {
        localId = buffer.getShort();
        queuePairNumber = buffer.getInt();
    }

    short getLocalId() {
        return localId;
    }

    int getQueuePairNumber() {
        return queuePairNumber;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Connection.class.getSimpleName() + "[", "]")
                .add("localId=" + localId)
                .add("queuePairNumber=" + queuePairNumber)
                .toString();
    }
}
