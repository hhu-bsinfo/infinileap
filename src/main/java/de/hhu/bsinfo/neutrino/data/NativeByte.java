package de.hhu.bsinfo.neutrino.data;

import java.nio.ByteBuffer;

public class NativeByte extends NativeDataType {

    public NativeByte(final ByteBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    public void set(final byte value) {
        getByteBuffer().put(getOffset(), value);
    }

    public byte get() {
        return getByteBuffer().get(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
