package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import de.hhu.bsinfo.neutrino.util.flag.LongFlag;

public class NativeIntegerBitMask<T extends Enum<T> & IntegerFlag> extends NativeDataType {

    public NativeIntegerBitMask(LocalBuffer byteBuffer, final long offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Integer.BYTES;
    }

    public final int get() {
        return getByteBuffer().getInt(getOffset());
    }

    @SafeVarargs
    public final void set(final T... flags) {
        getByteBuffer().putInt(getOffset(), get() | BitMask.intOf(flags));
    }

    public final boolean isSet(final T flag) {
        return BitMask.isSet(get(), flag);
    }

    public void clear() {
        getByteBuffer().putInt(getOffset(), 0);
    }

    @Override
    public String toString() {
        return super.toString() + " " + String.format("%32s", Integer.toBinaryString(get())).replace(' ', '0');
    }
}
