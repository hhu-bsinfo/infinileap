package de.hhu.bsinfo.neutrino.struct.field;

import org.agrona.concurrent.AtomicBuffer;

public abstract class NativeDataType {

    /**
     * The backing buffer.
     */
    private final AtomicBuffer buffer;

    /**
     * The value's offset within the backing buffer.
     */
    private final int offset;

    protected NativeDataType(final AtomicBuffer buffer, final int offset) {
        this.buffer = buffer;
        this.offset = offset;
    }

    protected AtomicBuffer getBuffer() {
        return buffer;
    }

    public int getOffset() {
        return offset;
    }

    public abstract long getSize();

    @Override
    public String toString() {
        return String.format("[%d]", offset);
    }
}
