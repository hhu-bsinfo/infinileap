package de.hhu.bsinfo.infinileap;

import static org.linux.rdma.ibverbs_h.*;

import de.hhu.bsinfo.infinileap.util.Struct;
import jdk.incubator.foreign.MemoryAddress;

public final class ReceiveWorkRequest extends Struct {

    public ReceiveWorkRequest() {
        super(ibv_recv_wr.allocate());
    }

    public ReceiveWorkRequest(MemoryAddress address) {
        super(address, ibv_recv_wr.$LAYOUT());
    }

    public long getWorkRequestId() {
        return ibv_recv_wr.wr_id$get(segment());
    }

    public MemoryAddress getNext() {
        return ibv_recv_wr.next$get(segment());
    }

    public MemoryAddress getScatterGatherList() {
        return ibv_recv_wr.sg_list$get(segment());
    }

    public int getScatterGatherCount() {
        return ibv_recv_wr.num_sge$get(segment());
    }

    public void setWorkRequestId(final long value) {
        ibv_recv_wr.wr_id$set(segment(), value);
    }

    public void setNext(final MemoryAddress value) {
        ibv_recv_wr.next$set(segment(), value);
    }

    public void setScatterGatherList(final MemoryAddress value) {
        ibv_recv_wr.sg_list$set(segment(), value);
    }

    public void setScatterGatherCount(final int value) {
        ibv_recv_wr.num_sge$set(segment(), value);
    }
}
