package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import java.nio.ByteBuffer;

public class NativeBoolean extends NativeDataType {

    private static final byte FALSE = 0;
    private static final byte TRUE = 1;

    public NativeBoolean(LocalBuffer byteBuffer, final long offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Byte.BYTES;
    }

    @SuppressWarnings("BooleanParameter")
    public void set(final boolean value) {
        getByteBuffer().put(getOffset(), value ? TRUE : FALSE);
    }

    public boolean get() {
        return getByteBuffer().get(getOffset()) == TRUE;
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
