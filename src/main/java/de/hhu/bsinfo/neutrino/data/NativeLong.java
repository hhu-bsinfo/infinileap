package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import java.nio.ByteBuffer;

public class NativeLong extends NativeDataType {

    public NativeLong(final LocalBuffer byteBuffer, final long offset) {
        super(byteBuffer, offset);
    }

    public void set(final long value) {
        getByteBuffer().putLong(getOffset(), value);
    }

    public long get() {
        return getByteBuffer().getLong(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
