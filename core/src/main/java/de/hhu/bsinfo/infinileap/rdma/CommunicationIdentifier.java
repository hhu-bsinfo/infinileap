package de.hhu.bsinfo.infinileap.rdma;

import static org.linux.rdma.infinileap_h.*;

import de.hhu.bsinfo.infinileap.util.Status;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.CSupport;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public final class CommunicationIdentifier extends NativeObject {

    private static final int DEFAULT_BACKLOG = 100;

    private final EventChannel channel;

    public CommunicationIdentifier() {
        super(rdma_cm_id.allocate());
        this.channel = new EventChannel(rdma_cm_id.channel$get(segment()));
    }

    public CommunicationIdentifier(MemoryAddress address) {
        super(address, rdma_cm_id.$LAYOUT());
        this.channel = new EventChannel(rdma_cm_id.channel$get(segment()));
    }

    public MemoryAddress getVerbs() {
        return rdma_cm_id.verbs$get(segment());
    }

    public EventChannel getChannel() {
        return channel;
    }

    public MemoryAddress getContext() {
        return rdma_cm_id.context$get(segment());
    }

    public MemoryAddress getQueuePair() {
        return rdma_cm_id.qp$get(segment());
    }

    public MemorySegment getRoute() {
        return rdma_cm_id.route$slice(segment());
    }

    public int getPortSpace() {
        return rdma_cm_id.ps$get(segment());
    }

    public byte getPortNumber() {
        return rdma_cm_id.port_num$get(segment());
    }

    public MemoryAddress getEvent() {
        return rdma_cm_id.event$get(segment());
    }

    public MemoryAddress getSendCompletionChannel() {
        return rdma_cm_id.send_cq_channel$get(segment());
    }

    public MemoryAddress getSendCompletionQueue() {
        return rdma_cm_id.send_cq$get(segment());
    }

    public MemoryAddress getReceiveCompletionChannel() {
        return rdma_cm_id.recv_cq_channel$get(segment());
    }

    public MemoryAddress getReceiveCompletionQueue() {
        return rdma_cm_id.recv_cq$get(segment());
    }

    public MemoryAddress getSharedReceiveQueue() {
        return rdma_cm_id.srq$get(segment());
    }

    public MemoryAddress getProtectionDomain() {
        return rdma_cm_id.pd$get(segment());
    }

    public int getQueuePairType() {
        return rdma_cm_id.qp_type$get(segment());
    }

    public void setVerbs(final MemoryAddress value) {
        rdma_cm_id.verbs$set(segment(), value);
    }

    public void setChannel(final MemoryAddress value) {
        rdma_cm_id.channel$set(segment(), value);
    }

    public void setContext(final MemoryAddress value) {
        rdma_cm_id.context$set(segment(), value);
    }

    public void setQueuePair(final MemoryAddress value) {
        rdma_cm_id.qp$set(segment(), value);
    }

    public void setPortSpace(final int value) {
        rdma_cm_id.ps$set(segment(), value);
    }

    public void setPortNumber(final byte value) {
        rdma_cm_id.port_num$set(segment(), value);
    }

    public void setEvent(final MemoryAddress value) {
        rdma_cm_id.event$set(segment(), value);
    }

    public void setSendCompletionChannel(final MemoryAddress value) {
        rdma_cm_id.send_cq_channel$set(segment(), value);
    }

    public void setSendCompletionQueue(final MemoryAddress value) {
        rdma_cm_id.send_cq$set(segment(), value);
    }

    public void setReceiveCompletionChannel(final MemoryAddress value) {
        rdma_cm_id.recv_cq_channel$set(segment(), value);
    }

    public void setReceiveCompletionQueue(final MemoryAddress value) {
        rdma_cm_id.recv_cq$set(segment(), value);
    }

    public void setSharedReceiveQueue(final MemoryAddress value) {
        rdma_cm_id.srq$set(segment(), value);
    }

    public void setProtectionDomain(final MemoryAddress value) {
        rdma_cm_id.pd$set(segment(), value);
    }

    public void setQueuePairType(final int value) {
        rdma_cm_id.qp_type$set(segment(), value);
    }

    public void listen() {
        var status = rdma_listen(this, DEFAULT_BACKLOG);
        if (status != Status.OK) {
            throw new RuntimeException(Status.getErrorMessage());
        }
    }

    public CommunicationIdentifier getRequest() {
        try (var pointer = MemorySegment.allocateNative(CSupport.C_POINTER)) {
            var status = rdma_get_request(this, pointer);
            if (status != Status.OK) {
                throw new RuntimeException(Status.getErrorMessage());
            }

            return new CommunicationIdentifier(MemoryAccess.getAddress(pointer));
        }
    }

    public void accept(CommunicationIdentifier client) {
        var status = rdma_accept(client, MemoryAddress.NULL);
        if (status != Status.OK) {
            throw new RuntimeException(Status.getErrorMessage());
        }
    }

    public void connect() {
        var status = rdma_connect(this, MemoryAddress.NULL);
        if (status != Status.OK) {
            throw new RuntimeException(Status.getErrorMessage());
        }
    }

    @Override
    public void close() {
        rdma_destroy_ep(this);
        super.close();
    }
}
