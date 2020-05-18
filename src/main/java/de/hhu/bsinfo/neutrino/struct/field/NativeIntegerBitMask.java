package de.hhu.bsinfo.neutrino.struct.field;

import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import org.agrona.concurrent.AtomicBuffer;

public class NativeIntegerBitMask<T extends Enum<T> & IntegerFlag> extends NativeDataType {

    public NativeIntegerBitMask(AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Integer.BYTES;
    }

    public final int get() {
        return getBuffer().getInt(getOffset());
    }

    @SafeVarargs
    public final void set(final T... flags) {
        getBuffer().putInt(getOffset(), get() | BitMask.intOf(flags));
    }

    public final boolean isSet(final T flag) {
        return BitMask.isSet(get(), flag);
    }

    public void clear() {
        getBuffer().putInt(getOffset(), 0);
    }

    @Override
    public String toString() {
        return super.toString() + " " + String.format("%32s", Integer.toBinaryString(get())).replace(' ', '0');
    }
}
