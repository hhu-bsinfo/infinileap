package de.hhu.bsinfo.neutrino.struct.field;

import org.agrona.concurrent.AtomicBuffer;

public class NativeShort extends NativeDataType {

    public NativeShort(final AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Short.BYTES;
    }

    public void set(final short value) {
        getBuffer().putShort(getOffset(), value);
    }

    public short get() {
        return getBuffer().getShort(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
