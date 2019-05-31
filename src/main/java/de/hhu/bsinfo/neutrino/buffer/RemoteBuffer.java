package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.util.BufferUtil;
import de.hhu.bsinfo.neutrino.verbs.QueuePair;
import de.hhu.bsinfo.neutrino.verbs.SendFlag;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest.OpCode;

public class RemoteBuffer {

    private static final int SINGLE_ELEMENT = 1;

    private final QueuePair queuePair;
    private final long address;
    private final int key;

    public RemoteBuffer(QueuePair queuePair, long address, int key) {
        this.queuePair = queuePair;
        this.address = address;
        this.key = key;
    }

    public void read(RegisteredBuffer localBuffer) {
        execute(OpCode.RDMA_READ, localBuffer);
    }

    public void write(RegisteredBuffer localBuffer) {
        execute(OpCode.RDMA_WRITE, localBuffer);
    }

    private void execute(final OpCode operation, RegisteredBuffer buffer) {
        var elements = BufferUtil.split(buffer);

        var request = new SendWorkRequest(config -> {
            config.setOpCode(operation);
            config.rdma.setRemoteAddress(address);
            config.rdma.setRemoteKey(key);
            config.setFlags(SendFlag.SIGNALED);
            config.setListHandle(elements.getHandle());
            config.setListLength(SINGLE_ELEMENT);
        });

        queuePair.postSend(request);
    }

    @Override
    public String toString() {
        return "RemoteBuffer {" +
            "\n\taddress=" + address +
            ",\n\tkey=" + key +
            "\n}";
    }
}
