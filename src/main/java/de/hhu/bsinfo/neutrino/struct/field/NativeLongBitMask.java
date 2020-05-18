package de.hhu.bsinfo.neutrino.struct.field;

import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.flag.LongFlag;
import org.agrona.concurrent.AtomicBuffer;

public class NativeLongBitMask<T extends Enum<T> & LongFlag> extends NativeDataType {

    public NativeLongBitMask(AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Long.BYTES;
    }

    public final long get() {
        return getBuffer().getLong(getOffset());
    }

    @SafeVarargs
    public final void set(final T... flags) {
        getBuffer().putLong(getOffset(), get() | BitMask.longOf(flags));
    }

    public final boolean isSet(final T flag) {
        return BitMask.isSet(get(), flag);
    }

    public void clear() {
        getBuffer().putLong(getOffset(), 0);
    }

    @Override
    public String toString() {
        return super.toString() + " " + String.format("%64s", Long.toBinaryString(get())).replace(' ', '0');
    }
}
