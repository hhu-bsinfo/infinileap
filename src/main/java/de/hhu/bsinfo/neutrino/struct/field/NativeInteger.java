package de.hhu.bsinfo.neutrino.struct.field;

import org.agrona.concurrent.AtomicBuffer;

public class NativeInteger extends NativeDataType {

    public NativeInteger(final AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Integer.BYTES;
    }

    public void set(final int value) {
        getBuffer().putInt(getOffset(), value);
    }

    public int get() {
        return getBuffer().getInt(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }

    public String toHexString() {
        return super.toString() + " 0x" + Integer.toHexString(get());
    }
}
