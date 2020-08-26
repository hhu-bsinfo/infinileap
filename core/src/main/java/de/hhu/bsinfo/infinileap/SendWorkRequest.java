package de.hhu.bsinfo.infinileap;

import static org.linux.rdma.infinileap_h.*;

import de.hhu.bsinfo.infinileap.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;

public final class SendWorkRequest extends Struct {

    private final Rdma rdma = new Rdma(ibv_send_wr.wr$slice(segment()));
    private final Atomic atomic = new Atomic(ibv_send_wr.wr$slice(segment()));
    private final Unreliable unreliable = new Unreliable(ibv_send_wr.wr$slice(segment()));

    public SendWorkRequest() {
        super(ibv_send_wr.allocate());
    }

    public SendWorkRequest(MemoryAddress address) {
        super(address, ibv_send_wr.$LAYOUT());
    }

    public long getWorkRequestId() {
        return ibv_send_wr.wr_id$get(segment());
    }

    public MemoryAddress getNext() {
        return ibv_send_wr.next$get(segment());
    }

    public MemoryAddress getScatterGatherList() {
        return ibv_send_wr.sg_list$get(segment());
    }

    public int getScatterGatherCount() {
        return ibv_send_wr.num_sge$get(segment());
    }

    public int getOpcode() {
        return ibv_send_wr.opcode$get(segment());
    }

    public int getSendFlags() {
        return ibv_send_wr.send_flags$get(segment());
    }

    public int getImmediateData() {
        return ibv_send_wr.imm_data$get(segment());
    }

    public int getInvalidateRemoteKey() {
        return ibv_send_wr.invalidate_rkey$get(segment());
    }

    public void setWorkRequestId(final long value) {
        ibv_send_wr.wr_id$set(segment(), value);
    }

    public void setNext(final MemoryAddress value) {
        ibv_send_wr.next$set(segment(), value);
    }

    public void setScatterGatherList(final MemoryAddress value) {
        ibv_send_wr.sg_list$set(segment(), value);
    }

    public void setScatterGatherCount(final int value) {
        ibv_send_wr.num_sge$set(segment(), value);
    }

    public void setOpcode(final int value) {
        ibv_send_wr.opcode$set(segment(), value);
    }

    public void setSendFlags(final int value) {
        ibv_send_wr.send_flags$set(segment(), value);
    }

    public void setImmediateData(final int value) {
        ibv_send_wr.imm_data$set(segment(), value);
    }

    public void setInvalidateRemoteKey(final int value) {
        ibv_send_wr.invalidate_rkey$set(segment(), value);
    }

    public Rdma getRdma() {
        return rdma;
    }

    public Atomic getAtomic() {
        return atomic;
    }

    public Unreliable getUnreliable() {
        return unreliable;
    }

    public static final class Rdma extends Struct {

        public Rdma(MemoryAddress address, MemoryLayout layout) {
            super(address, ibv_send_wr.wr.rdma.$LAYOUT());
        }

        public Rdma(MemorySegment segment) {
            super(segment);
        }

        public long getRemoteAddress() {
            return ibv_send_wr.wr.rdma.remote_addr$get(segment());
        }

        public int getRemoteKey() {
            return ibv_send_wr.wr.rdma.rkey$get(segment());
        }
    }

    public static final class Atomic extends Struct {

        public Atomic(MemoryAddress address, MemoryLayout layout) {
            super(address, ibv_send_wr.wr.rdma.$LAYOUT());
        }

        public Atomic(MemorySegment segment) {
            super(segment);
        }

        public long getRemoteAddress() {
            return ibv_send_wr.wr.atomic.remote_addr$get(segment());
        }

        public long getCompareAdd() {
            return ibv_send_wr.wr.atomic.compare_add$get(segment());
        }

        public long getSwap() {
            return ibv_send_wr.wr.atomic.swap$get(segment());
        }

        public int getRemoteKey() {
            return ibv_send_wr.wr.atomic.rkey$get(segment());
        }
    }

    public static final class Unreliable extends Struct {

        public Unreliable(MemoryAddress address, MemoryLayout layout) {
            super(address, ibv_send_wr.wr.rdma.$LAYOUT());
        }

        public Unreliable(MemorySegment segment) {
            super(segment);
        }

        public MemoryAddress getAddressHandle() {
            return ibv_send_wr.wr.ud.ah$get(segment());
        }

        public int getRemoteQueuePairNumber() {
            return ibv_send_wr.wr.ud.remote_qpn$get(segment());
        }

        public int getRemoteQueuePairKey() {
            return ibv_send_wr.wr.ud.remote_qkey$get(segment());
        }
    }
}
