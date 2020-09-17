package de.hhu.bsinfo.infinileap.verbs;

import de.hhu.bsinfo.infinileap.buffer.Buffer;
import de.hhu.bsinfo.infinileap.util.*;
import jdk.incubator.foreign.MemoryAddress;
import org.linux.rdma.infinileap_h.*;

import java.io.IOException;

import static org.linux.rdma.infinileap_h.*;

public class ProtectionDomain extends NativeObject {

    public ProtectionDomain() {
        super(ibv_pd.allocate());
    }

    public ProtectionDomain(MemoryAddress address) {
        super(address, ibv_pd.$LAYOUT());
    }

    public MemoryAddress getContext() {
        return ibv_pd.context$get(segment());
    }

    public int getHandle() {
        return ibv_pd.handle$get(segment());
    }

    public void setContext(final MemoryAddress value) {
        ibv_pd.context$set(segment(), value);
    }

    public void setHandle(final int value) {
        ibv_pd.handle$set(segment(), value);
    }

    public Buffer allocateMemory(long capacity, MemoryAlignment alignment, AccessFlag... accessFlags) {
        var segment = MemoryUtil.allocateMemory(capacity);
        var region = new MemoryRegion(ibv_reg_mr(this, segment, capacity, BitMask.intOf(accessFlags)));
        return new Buffer(segment, region);
    }

    @Override
    public void close() {
        ibv_dealloc_pd(this);
        super.close();
    }

    public QueuePair createQueuePair(QueuePair.InitialAttributes initialAttributes) throws IOException {
        var address = ibv_create_qp(this, initialAttributes);
        if (address == MemoryAddress.NULL) {
            throw new IOException(Status.getErrorMessage());
        }
    }
}
