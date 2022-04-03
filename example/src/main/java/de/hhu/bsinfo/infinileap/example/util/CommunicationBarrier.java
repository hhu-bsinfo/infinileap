package de.hhu.bsinfo.infinileap.example.util;

import de.hhu.bsinfo.infinileap.binding.Request;
import de.hhu.bsinfo.infinileap.binding.Status;
import jdk.incubator.foreign.MemoryAddress;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public final class CommunicationBarrier {

    private final AtomicBoolean barrier = new AtomicBoolean(false);

    public final void release(long request, Status status, MemoryAddress data) {
        log.info("Status is {}", status.toString());
        barrier.set(true);
    }

    public final void release(long request, Status status, MemoryAddress tagInfo, MemoryAddress data) {
        log.info("Status is {}", status.toString());
        barrier.set(true);
    }

    public final void reset() {
        barrier.set(false);
    }

    public final boolean isReleased() {
        return barrier.get();
    }
}
