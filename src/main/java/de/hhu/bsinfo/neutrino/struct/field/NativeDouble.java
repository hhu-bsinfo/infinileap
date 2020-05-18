package de.hhu.bsinfo.neutrino.struct.field;

import org.agrona.concurrent.AtomicBuffer;

public class NativeDouble extends NativeDataType {

    public NativeDouble(final AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Double.BYTES;
    }

    public void set(final double value) {
        getBuffer().putDouble(getOffset(), value);
    }

    public double get() {
        return getBuffer().getDouble(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
