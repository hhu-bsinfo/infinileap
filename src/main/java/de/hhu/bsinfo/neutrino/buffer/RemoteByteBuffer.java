package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;
import de.hhu.bsinfo.neutrino.verbs.QueuePair;
import de.hhu.bsinfo.neutrino.verbs.ScatterGatherElement;
import de.hhu.bsinfo.neutrino.verbs.SendFlag;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest.OpCode;

public class RemoteByteBuffer {

    private static final int SINGLE_ELEMENT = 1;

    private final QueuePair queuePair;
    private final long address;
    private final int key;

    public RemoteByteBuffer(QueuePair queuePair, long address, int key) {
        this.queuePair = queuePair;
        this.address = address;
        this.key = key;
    }

    public void read(LocalByteBuffer localBuffer) {
        execute(OpCode.RDMA_READ, localBuffer);
    }

    public void write(LocalByteBuffer localBuffer) {
        execute(OpCode.RDMA_WRITE, localBuffer);
    }

    private void execute(final OpCode operation, LocalByteBuffer localBuffer) {
        var element = new ScatterGatherElement(config -> {
            config.setAddress(localBuffer.getHandle());
            config.setLength(localBuffer.capacity());
            config.setLocalKey(localBuffer.getLocalKey());
        });

        var request = new SendWorkRequest(config -> {
            config.setOpCode(operation);
            config.rdma.setRemoteAddress(address);
            config.rdma.setRemoteKey(key);
            config.setFlags(SendFlag.SIGNALED);
            config.setListHandle(element.getHandle());
            config.setListLength(SINGLE_ELEMENT);
        });

        queuePair.postSend(request);
    }

    @Override
    public String toString() {
        return "RemoteByteBuffer {" +
            "\n\taddress=" + address +
            ",\n\tkey=" + key +
            "\n}";
    }
}
