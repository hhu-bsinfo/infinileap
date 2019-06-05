package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import java.nio.ByteBuffer;

public class NativeShort extends NativeDataType {

    public NativeShort(final LocalBuffer byteBuffer, final long offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Short.BYTES;
    }

    public void set(final short value) {
        getByteBuffer().putShort(getOffset(), value);
    }

    public short get() {
        return getByteBuffer().getShort(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
