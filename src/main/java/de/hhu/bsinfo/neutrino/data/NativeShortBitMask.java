package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.Flag;

public class NativeShortBitMask<T extends Enum<T> & Flag> extends NativeDataType {

    public NativeShortBitMask(LocalBuffer byteBuffer, final long offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Short.BYTES;
    }

    public final short get() {
        return getByteBuffer().getShort(getOffset());
    }

    @SafeVarargs
    public final void set(final T... flags) {
        getByteBuffer().putShort(getOffset(), (short) (get() | BitMask.shortOf(flags)));
    }

    public final boolean isSet(final T flag) {
        return BitMask.isSet(get(), flag);
    }

    public void clear() {
        getByteBuffer().putShort(getOffset(), (short) 0);
    }

    @Override
    public String toString() {
        return super.toString() + " " + String.format("%16s", Integer.toBinaryString(get())).replace(' ', '0');
    }
}
