package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.Flag;

public class NativeLongBitMask<T extends Enum<T> & Flag> extends NativeDataType {

    public NativeLongBitMask(LocalBuffer byteBuffer, final long offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Long.BYTES;
    }

    public final long get() {
        return getByteBuffer().getLong(getOffset());
    }

    @SafeVarargs
    public final void set(final T... flags) {
        getByteBuffer().putLong(getOffset(), get() | BitMask.longOf(flags));
    }

    public final boolean isSet(final T flag) {
        return BitMask.isSet(get(), flag);
    }

    public void clear() {
        getByteBuffer().putLong(getOffset(), 0);
    }

    @Override
    public String toString() {
        return super.toString() + " " + String.format("%64s", Long.toBinaryString(get())).replace(' ', '0');
    }
}
