package de.hhu.bsinfo.neutrino.data;

import java.nio.ByteBuffer;

public class NativeShort extends NativeDataType {

    public NativeShort(final ByteBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    public void set(final short value) {
        getByteBuffer().putShort(getOffset(), value);
    }

    public short get() {
        return getByteBuffer().getShort(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
