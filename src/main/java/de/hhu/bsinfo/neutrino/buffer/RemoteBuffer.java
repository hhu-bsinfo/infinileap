package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.verbs.QueuePair;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest.OpCode;
import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest.SendFlag;

public class RemoteBuffer {

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

    public long read(RegisteredBuffer localBuffer) {
        return execute(OpCode.RDMA_READ, localBuffer);
    }

    public long read(long index, RegisteredBuffer buffer, long offset, long length) {
        return execute(OpCode.RDMA_READ, index, buffer, offset, length);
    }

    public long write(RegisteredBuffer localBuffer) {
        return execute(OpCode.RDMA_WRITE, localBuffer);
    }

    public long write(long index, RegisteredBuffer buffer, long offset, long length) {
        return execute(OpCode.RDMA_WRITE, index, buffer, offset, length);
    }

    private long execute(final OpCode operation, RegisteredBuffer buffer) {
        return execute(operation, 0, buffer, 0, buffer.capacity());
    }

    private long execute(final OpCode operation, long index, RegisteredBuffer buffer, long offset, long length) {
        var elements = buffer.split(offset, length);

        var request = new SendWorkRequest.RdmaBuilder(operation, elements, address + index, key)
                .withSendFlags(SendFlag.SIGNALED)
                .build();

        queuePair.postSend(request);

        return request.getId();
    }

    public long capacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "RemoteBuffer {" +
            ",\n\taddress=" + address +
            ",\n\tcapacity=" + capacity +
            ",\n\tkey=" + key +
            "\n}";
    }
}
