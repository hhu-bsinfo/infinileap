package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;

public abstract class NativeDataType {

    private final LocalBuffer byteBuffer;
    private final long offset;

    public NativeDataType(final LocalBuffer byteBuffer, final long offset) {
        this.byteBuffer = byteBuffer;
        this.offset = offset;
    }

    protected LocalBuffer getByteBuffer() {
        return byteBuffer;
    }

    public long getOffset() {
        return offset;
    }

    public abstract long getSize();

    @Override
    public String toString() {
        return String.format("[%d]", offset);
    }
}
