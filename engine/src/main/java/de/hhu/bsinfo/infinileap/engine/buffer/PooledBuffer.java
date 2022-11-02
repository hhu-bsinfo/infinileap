package de.hhu.bsinfo.infinileap.engine.buffer;

import de.hhu.bsinfo.infinileap.engine.message.Callback;

import java.lang.foreign.MemorySegment;
import java.util.function.IntConsumer;

public final class PooledBuffer {

    /**
     * This pooled buffer's backing memory segment slice.
     */
    private final MemorySegment segment;

    /**
     * This pooled buffer's identifier.
     */
    private final int identifier;

    /**
     * Function for returning this pooled buffer to its pool.
     */
    private final IntConsumer releaser;

    /**
     * The client's callback instance.
     */
    private Callback<Void> callback;

    public PooledBuffer(int identifier, MemorySegment segment, IntConsumer releaser) {
        this.identifier = identifier;
        this.releaser = releaser;
        this.segment = segment;
    }

    public MemorySegment segment() {
        return segment;
    }

    public int identifier() {
        return identifier;
    }

    public void release() {
        releaser.accept(identifier);
    }

    public void setCallback(Callback<Void> callback) {
        this.callback = callback;
    }

    public Callback<Void> getCallback() {
        return callback;
    }
}
