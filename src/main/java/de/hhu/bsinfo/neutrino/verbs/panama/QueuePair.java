package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import static org.linux.rdma.ibverbs_h.*;

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

    public static final class Attributes extends Struct {

        public Attributes() {
            super(ibv_qp_attr.allocate());
        }

        public Attributes(MemoryAddress address) {
            super(address, ibv_qp_attr.$LAYOUT());
        }

        public int getQueuePairState() {
            return ibv_qp_attr.qp_state$get(segment());
        }

        public int getCurrentQueuePairState() {
            return ibv_qp_attr.cur_qp_state$get(segment());
        }

        public int getPathMtu() {
            return ibv_qp_attr.path_mtu$get(segment());
        }

        public int getPathMigrationState() {
            return ibv_qp_attr.path_mig_state$get(segment());
        }

        public int getQueueKey() {
            return ibv_qp_attr.qkey$get(segment());
        }

        public int getReceiveQueuePacketSerialNumber() {
            return ibv_qp_attr.rq_psn$get(segment());
        }

        public int getSendQueuePacketSerialNumber() {
            return ibv_qp_attr.sq_psn$get(segment());
        }

        public int getDestinationQueuePairNumber() {
            return ibv_qp_attr.dest_qp_num$get(segment());
        }

        public int getQueuePairAccessFlags() {
            return ibv_qp_attr.qp_access_flags$get(segment());
        }

        public MemorySegment getCapabilities() {
            return ibv_qp_attr.cap$addr(segment());
        }

        public MemorySegment getPrimaryPathAddressVector() {
            return ibv_qp_attr.ah_attr$addr(segment());
        }

        public MemorySegment getAlternativePathAddressVector() {
            return ibv_qp_attr.alt_ah_attr$addr(segment());
        }

        public short getPrimaryPartitionKeyIndex() {
            return ibv_qp_attr.pkey_index$get(segment());
        }

        public short getAlternativePartitionKeyIndex() {
            return ibv_qp_attr.alt_pkey_index$get(segment());
        }

        public boolean isSendQueueDrainedNotifyEnabled() {
            return ibv_qp_attr.en_sqd_async_notify$get(segment()) == Verbs.TRUE;
        }

        public boolean isSendQueueDraining() {
            return ibv_qp_attr.sq_draining$get(segment()) == Verbs.TRUE;
        }

        public byte getOutstandingDestinationAtomicOperations() {
            return ibv_qp_attr.max_rd_atomic$get(segment());
        }

        public byte getMaxDestinationAtomicResources() {
            return ibv_qp_attr.max_dest_rd_atomic$get(segment());
        }

        public byte getMinReceiverNotReadyTimer() {
            return ibv_qp_attr.min_rnr_timer$get(segment());
        }

        public byte getPortNumber() {
            return ibv_qp_attr.port_num$get(segment());
        }

        public byte getTimeout() {
            return ibv_qp_attr.timeout$get(segment());
        }

        public byte getRetryCount() {
            return ibv_qp_attr.retry_cnt$get(segment());
        }

        public byte getReceiverNotReadyRetry() {
            return ibv_qp_attr.rnr_retry$get(segment());
        }

        public byte getAlternativePortNumber() {
            return ibv_qp_attr.alt_port_num$get(segment());
        }

        public byte getAlternativeTimeout() {
            return ibv_qp_attr.alt_timeout$get(segment());
        }

        public int getRateLimit() {
            return ibv_qp_attr.rate_limit$get(segment());
        }

        public void setQueuePairState(final int value) {
            ibv_qp_attr.qp_state$set(segment(), value);
        }

        public void setCurrentQueuePairState(final int value) {
            ibv_qp_attr.cur_qp_state$set(segment(), value);
        }

        public void setPathMtu(final int value) {
            ibv_qp_attr.path_mtu$set(segment(), value);
        }

        public void setPathMigrationState(final int value) {
            ibv_qp_attr.path_mig_state$set(segment(), value);
        }

        public void setQueueKey(final int value) {
            ibv_qp_attr.qkey$set(segment(), value);
        }

        public void setReceiveQueuePacketSerialNumber(final int value) {
            ibv_qp_attr.rq_psn$set(segment(), value);
        }

        public void setSendQueuePacketSerialNumber(final int value) {
            ibv_qp_attr.sq_psn$set(segment(), value);
        }

        public void setDestinationQueuePairNumber(final int value) {
            ibv_qp_attr.dest_qp_num$set(segment(), value);
        }

        public void setQueuePairAccessFlags(final int value) {
            ibv_qp_attr.qp_access_flags$set(segment(), value);
        }

        public void setPrimaryPartitionKeyIndex(final short value) {
            ibv_qp_attr.pkey_index$set(segment(), value);
        }

        public void setAlternativePartitionKeyIndex(final short value) {
            ibv_qp_attr.alt_pkey_index$set(segment(), value);
        }

        public void setSendQueueDrainedNotifyEnabled(final boolean value) {
            ibv_qp_attr.en_sqd_async_notify$set(segment(), Verbs.toByte(value));
        }

        public void setSendQueueDraining(final boolean value) {
            ibv_qp_attr.sq_draining$set(segment(), Verbs.toByte(value));
        }

        public void setOutstandingDestinationAtomicOperations(final byte value) {
            ibv_qp_attr.max_rd_atomic$set(segment(), value);
        }

        public void setMaxDestinationAtomicResources(final byte value) {
            ibv_qp_attr.max_dest_rd_atomic$set(segment(), value);
        }

        public void setMinReceiverNotReadyTimer(final byte value) {
            ibv_qp_attr.min_rnr_timer$set(segment(), value);
        }

        public void setPortNumber(final byte value) {
            ibv_qp_attr.port_num$set(segment(), value);
        }

        public void setTimeout(final byte value) {
            ibv_qp_attr.timeout$set(segment(), value);
        }

        public void setRetryCount(final byte value) {
            ibv_qp_attr.retry_cnt$set(segment(), value);
        }

        public void setReceiverNotReadyRetry(final byte value) {
            ibv_qp_attr.rnr_retry$set(segment(), value);
        }

        public void setAlternativePortNumber(final byte value) {
            ibv_qp_attr.alt_port_num$set(segment(), value);
        }

        public void setAlternativeTimeout(final byte value) {
            ibv_qp_attr.alt_timeout$set(segment(), value);
        }

        public void setRateLimit(final int value) {
            ibv_qp_attr.rate_limit$set(segment(), value);
        }
    }
}
