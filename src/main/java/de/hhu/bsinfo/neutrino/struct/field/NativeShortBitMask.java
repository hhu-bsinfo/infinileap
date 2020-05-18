package de.hhu.bsinfo.neutrino.struct.field;

import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.flag.ShortFlag;
import org.agrona.concurrent.AtomicBuffer;

public class NativeShortBitMask<T extends Enum<T> & ShortFlag> extends NativeDataType {

    public NativeShortBitMask(AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Short.BYTES;
    }

    public final short get() {
        return getBuffer().getShort(getOffset());
    }

    @SafeVarargs
    public final void set(final T... flags) {
        getBuffer().putShort(getOffset(), (short) (get() | BitMask.shortOf(flags)));
    }

    public final boolean isSet(final T flag) {
        return BitMask.isSet(get(), flag);
    }

    public void clear() {
        getBuffer().putShort(getOffset(), (short) 0);
    }

    @Override
    public String toString() {
        return super.toString() + " " + String.format("%16s", Integer.toBinaryString(get())).replace(' ', '0');
    }
}
