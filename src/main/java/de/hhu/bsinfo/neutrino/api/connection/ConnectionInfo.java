package de.hhu.bsinfo.neutrino.api.connection;

import java.nio.ByteBuffer;
import java.util.StringJoiner;

public class ConnectionInfo {

    private short localId;
    private int queuePairNumber;

    public ConnectionInfo() {}

    public ConnectionInfo(short localId, int queuePairNumber) {
        this.localId = localId;
        this.queuePairNumber = queuePairNumber;
    }

    public ConnectionInfo(ByteBuffer buffer) {
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
        return new StringJoiner(", ", ConnectionInfo.class.getSimpleName() + "[", "]")
                .add("localId=" + localId)
                .add("queuePairNumber=" + queuePairNumber)
                .toString();
    }
}
