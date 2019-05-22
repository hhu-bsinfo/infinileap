package de.hhu.bsinfo.neutrino.data;

import java.nio.ByteBuffer;

public class NativeBoolean extends NativeDataType {

    private static final int FALSE = 0;
    private static final int TRUE = 1;

    public NativeBoolean(ByteBuffer byteBuffer, int offset) {
        super(byteBuffer, offset);
    }

    @SuppressWarnings("BooleanParameter")
    public void set(final boolean value) {
        getByteBuffer().putInt(getOffset(), value ? TRUE : FALSE);
    }

    public boolean get() {
        return getByteBuffer().getInt(getOffset()) == TRUE;
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
