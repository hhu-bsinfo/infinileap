package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.verbs.QueuePair;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest.OpCode;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest.SendFlag;

public class RemoteBuffer {

    private static final int SINGLE_ELEMENT = 1;

    private final QueuePair queuePair;
    private final long address;
    private final long capacity;
    private final int key;

    public RemoteBuffer(QueuePair queuePair, long address, long capacity, int key) {
        this.queuePair = queuePair;
        this.address = address;
        this.capacity = capacity;
        this.key = key;
    }

    public void read(RegisteredBuffer localBuffer) {
        execute(OpCode.RDMA_READ, localBuffer);
    }

    public void read(long index, RegisteredBuffer buffer, long offset, long length) {
        execute(OpCode.RDMA_READ, index, buffer, offset, length);
    }

    public void write(RegisteredBuffer localBuffer) {
        execute(OpCode.RDMA_WRITE, localBuffer);
    }

    public void write(long index, RegisteredBuffer buffer, long offset, long length) {
        execute(OpCode.RDMA_WRITE, index, buffer, offset, length);
    }

    private void execute(final OpCode operation, RegisteredBuffer buffer) {
        execute(operation, 0, buffer, 0, buffer.capacity());
    }

    private void execute(final OpCode operation, long index, RegisteredBuffer buffer, long offset, long length) {
        var elements = buffer.split(offset, length);

        var request = new SendWorkRequest(config -> {
            config.setOpCode(operation);
            config.rdma.setRemoteAddress(address + index);
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
