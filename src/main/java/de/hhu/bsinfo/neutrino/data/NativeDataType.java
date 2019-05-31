package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;

public class NativeDataType {

    private final LocalBuffer byteBuffer;
    private final int offset;

    public NativeDataType(final LocalBuffer byteBuffer, final int offset) {
        this.byteBuffer = byteBuffer;
        this.offset = offset;
    }

    protected LocalBuffer getByteBuffer() {
        return byteBuffer;
    }

    protected int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return String.format("[%d]", offset);
    }
}
