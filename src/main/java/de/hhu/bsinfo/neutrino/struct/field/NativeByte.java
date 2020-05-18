package de.hhu.bsinfo.neutrino.struct.field;

import org.agrona.concurrent.AtomicBuffer;

public class NativeByte extends NativeDataType {

    public NativeByte(final AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Byte.BYTES;
    }

    public void set(final byte value) {
        getBuffer().putByte(getOffset(), value);
    }

    public byte get() {
        return getBuffer().getByte(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
