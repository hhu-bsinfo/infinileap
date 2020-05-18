package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.NativeLong;
import de.hhu.bsinfo.neutrino.struct.field.NativeShort;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.LinkNative;
import org.agrona.concurrent.AtomicBuffer;

@LinkNative("rdma_ib_addr")
public final class InfinibandAddress extends Struct {

    private final NativeLong source = longField("sgid");
    private final NativeLong destination = longField("dgid");
    private final NativeShort partitionKey = shortField("pkey");

    InfinibandAddress() {}

    InfinibandAddress(long handle) {
        super(handle);
    }

    InfinibandAddress(AtomicBuffer buffer, int offset) {
        super(buffer, offset);
    }

    public long getSource() {
        return source.get();
    }

    public long getDestination() {
        return destination.get();
    }

    public short getPartitionKey() {
        return partitionKey.get();
    }

    public void setSource(final long value) {
        source.set(value);
    }

    public void setDestination(final long value) {
        destination.set(value);
    }

    public void setPartitionKey(final short value) {
        partitionKey.set(value);
    }
}
