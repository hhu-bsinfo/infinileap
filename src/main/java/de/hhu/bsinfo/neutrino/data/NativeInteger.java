package de.hhu.bsinfo.neutrino.data;

import java.nio.ByteBuffer;

public class NativeInteger extends NativeDataType {

    public NativeInteger(final ByteBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    public void set(final int value) {
        getByteBuffer().putInt(getOffset(), value);
    }

    public int get() {
        return getByteBuffer().getInt(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
