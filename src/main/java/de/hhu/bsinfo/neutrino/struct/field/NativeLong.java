package de.hhu.bsinfo.neutrino.struct.field;

import org.agrona.concurrent.AtomicBuffer;

public class NativeLong extends NativeDataType {

    public NativeLong(final AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Long.BYTES;
    }

    public void set(final long value) {
        getBuffer().putLong(getOffset(), value);
    }

    public long get() {
        return getBuffer().getLong(getOffset());
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }

    public String toHexString() {
        return super.toString() + " 0x" + Long.toHexString(get());
    }
}
