package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;

public class NativeDouble extends NativeDataType {

    public NativeDouble(final LocalBuffer byteBuffer, final long offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Double.BYTES;
    }

    public void set(final double value) {
        getByteBuffer().putDouble(getOffset(), value);
    }

    public double get() {
        return getByteBuffer().getDouble(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
