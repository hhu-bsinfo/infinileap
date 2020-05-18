package de.hhu.bsinfo.neutrino.struct.field;

import org.agrona.concurrent.AtomicBuffer;

public class NativeBoolean extends NativeDataType {

    private static final byte FALSE = 0;
    private static final byte TRUE = 1;

    public NativeBoolean(AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Byte.BYTES;
    }

    @SuppressWarnings("BooleanParameter")
    public void set(final boolean value) {
        getBuffer().putByte(getOffset(), value ? TRUE : FALSE);
    }

    public boolean get() {
        return getBuffer().getByte(getOffset()) == TRUE;
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
