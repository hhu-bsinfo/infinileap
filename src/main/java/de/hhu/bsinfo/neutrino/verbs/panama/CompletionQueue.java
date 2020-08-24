package de.hhu.bsinfo.neutrino.verbs.panama;

import static org.linux.rdma.ibverbs_h.*;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public final class CompletionQueue extends Struct {

    public CompletionQueue() {
        super(ibv_cq.allocate());
    }

    public CompletionQueue(MemoryAddress address) {
        super(address, ibv_cq.$LAYOUT());
    }

    public MemoryAddress getContext() {
        return ibv_cq.context$get(segment());
    }

    public MemoryAddress getChannel() {
        return ibv_cq.channel$get(segment());
    }

    public MemoryAddress getCompletionQueueContext() {
        return ibv_cq.cq_context$get(segment());
    }

    public int getHandle() {
        return ibv_cq.handle$get(segment());
    }

    public int getMinElementCount() {
        return ibv_cq.cqe$get(segment());
    }

    public MemorySegment getMutex() {
        return ibv_cq.mutex$slice(segment());
    }

    public MemorySegment getCondition() {
        return ibv_cq.cond$slice(segment());
    }

    public int getCompletionEventsCompleted() {
        return ibv_cq.comp_events_completed$get(segment());
    }

    public int getAsyncEventsCompleted() {
        return ibv_cq.async_events_completed$get(segment());
    }

    public void setContext(final MemoryAddress value) {
        ibv_cq.context$set(segment(), value);
    }

    public void setChannel(final MemoryAddress value) {
        ibv_cq.channel$set(segment(), value);
    }

    public void setCompletionQueueContext(final MemoryAddress value) {
        ibv_cq.cq_context$set(segment(), value);
    }

    public void setHandle(final int value) {
        ibv_cq.handle$set(segment(), value);
    }

    public void setMinElementCount(final int value) {
        ibv_cq.cqe$set(segment(), value);
    }

    public void setCompletionEventsCompleted(final int value) {
        ibv_cq.comp_events_completed$set(segment(), value);
    }

    public void setAsyncEventsCompleted(final int value) {
        ibv_cq.async_events_completed$set(segment(), value);
    }
}
