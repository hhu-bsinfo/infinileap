package de.hhu.bsinfo.neutrino.data;

import java.nio.ByteBuffer;

public class NativeDataType {

    private final ByteBuffer byteBuffer;
    private final int offset;

    public NativeDataType(final ByteBuffer byteBuffer, final int offset) {
        this.byteBuffer = byteBuffer;
        this.offset = offset;
    }

    protected ByteBuffer getByteBuffer() {
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
