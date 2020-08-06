package de.hhu.bsinfo.neutrino.verbs.panama;

import static org.linux.rdma.ibverbs_h.*;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public final class QueuePair extends Struct {

    public QueuePair() {
        super(ibv_qp.allocate());
    }

    public QueuePair(MemoryAddress address) {
        super(address, ibv_qp.$LAYOUT());
    }

    public MemoryAddress getContext() {
        return ibv_qp.context$get(segment());
    }

    public MemoryAddress getQueuPairContext() {
        return ibv_qp.qp_context$get(segment());
    }

    public MemoryAddress getProtectionDomain() {
        return ibv_qp.pd$get(segment());
    }

    public MemoryAddress getSendCompletionQueue() {
        return ibv_qp.send_cq$get(segment());
    }

    public MemoryAddress getReceiveCompletionQueue() {
        return ibv_qp.recv_cq$get(segment());
    }

    public MemoryAddress getSharedReceiveQueue() {
        return ibv_qp.srq$get(segment());
    }

    public int getHandle() {
        return ibv_qp.handle$get(segment());
    }

    public int getQueuePairNumber() {
        return ibv_qp.qp_num$get(segment());
    }

    public int getState() {
        return ibv_qp.state$get(segment());
    }

    public int getQueuePairType() {
        return ibv_qp.qp_type$get(segment());
    }

    public MemorySegment getMutex() {
        return ibv_qp.mutex$addr(segment());
    }

    public MemorySegment getCondition() {
        return ibv_qp.cond$addr(segment());
    }

    public int getEventsCompleted() {
        return ibv_qp.events_completed$get(segment());
    }

    public void setContext(final MemoryAddress value) {
        ibv_qp.context$set(segment(), value);
    }

    public void setQueuePairContext(final MemoryAddress value) {
        ibv_qp.qp_context$set(segment(), value);
    }

    public void setProtectionDomain(final MemoryAddress value) {
        ibv_qp.pd$set(segment(), value);
    }

    public void setSendCompletionQueue(final MemoryAddress value) {
        ibv_qp.send_cq$set(segment(), value);
    }

    public void setReceiveCompletionQueue(final MemoryAddress value) {
        ibv_qp.recv_cq$set(segment(), value);
    }

    public void setSharedReceiveQueue(final MemoryAddress value) {
        ibv_qp.srq$set(segment(), value);
    }

    public void setHandle(final int value) {
        ibv_qp.handle$set(segment(), value);
    }

    public void setQueuePairNumber(final int value) {
        ibv_qp.qp_num$set(segment(), value);
    }

    public void setState(final int value) {
        ibv_qp.state$set(segment(), value);
    }

    public void setQueuePairType(final int value) {
        ibv_qp.qp_type$set(segment(), value);
    }

    public void setEventsCompleted(final int value) {
        ibv_qp.events_completed$set(segment(), value);
    }

    public static final class InitialAttributes extends Struct {

        public InitialAttributes() {
            super(ibv_qp_init_attr.allocate());
        }

        public InitialAttributes(MemoryAddress address) {
            super(address, ibv_qp_init_attr.$LAYOUT());
        }

        public MemoryAddress getQueuePairContext() {
            return ibv_qp_init_attr.qp_context$get(segment());
        }

        public MemoryAddress getSendCompletionQueue() {
            return ibv_qp_init_attr.send_cq$get(segment());
        }

        public MemoryAddress getReceiveCompletionQueue() {
            return ibv_qp_init_attr.recv_cq$get(segment());
        }

        public MemoryAddress getSharedReceiveQueue() {
            return ibv_qp_init_attr.srq$get(segment());
        }

        public MemorySegment getCapabilities() {
            return ibv_qp_init_attr.cap$addr(segment());
        }

        public int getQueuePairType() {
            return ibv_qp_init_attr.qp_type$get(segment());
        }

        public int getSignalingLevel() {
            return ibv_qp_init_attr.sq_sig_all$get(segment());
        }

        public void setQueuePairContext(final MemoryAddress value) {
            ibv_qp_init_attr.qp_context$set(segment(), value);
        }

        public void setSendCompletionQueue(final MemoryAddress value) {
            ibv_qp_init_attr.send_cq$set(segment(), value);
        }

        public void setReceiveCompletionQueue(final MemoryAddress value) {
            ibv_qp_init_attr.recv_cq$set(segment(), value);
        }

        public void setSharedReceiveQueue(final MemoryAddress value) {
            ibv_qp_init_attr.srq$set(segment(), value);
        }

        public void setQueuePairType(final int value) {
            ibv_qp_init_attr.qp_type$set(segment(), value);
        }

        public void setSignalingLevel(final int value) {
            ibv_qp_init_attr.sq_sig_all$set(segment(), value);
        }
    }
}
