package de.hhu.bsinfo.infinileap.example.util;

import de.hhu.bsinfo.infinileap.binding.Request;
import de.hhu.bsinfo.infinileap.binding.Status;
import jdk.incubator.foreign.MemoryAddress;

import java.util.concurrent.atomic.AtomicBoolean;

public final class CommunicationBarrier {

    private final AtomicBoolean barrier = new AtomicBoolean(false);

    public final void release(Request request, Status status, MemoryAddress data) {
        barrier.set(true);
    }

    public final void release(Request request, Status status, MemoryAddress tagInfo, MemoryAddress data) {
        barrier.set(true);
    }

    public final void reset() {
        barrier.set(false);
    }

    public final boolean isReleased() {
        return barrier.get();
    }
}
