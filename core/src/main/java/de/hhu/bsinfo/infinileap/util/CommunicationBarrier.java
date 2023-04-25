package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.binding.Status;

import java.lang.foreign.MemorySegment;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CommunicationBarrier {

    private final AtomicBoolean barrier = new AtomicBoolean(false);

    public final void release(long request, Status status, MemorySegment data) {
        barrier.set(true);
    }

    public final void release(long request, Status status, MemorySegment tagInfo, MemorySegment data) {
        barrier.set(true);
    }

    public final void reset() {
        barrier.set(false);
    }

    public final boolean isReleased() {
        return barrier.get();
    }
}
