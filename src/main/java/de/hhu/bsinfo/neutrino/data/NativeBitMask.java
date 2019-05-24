package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.Flag;
import java.nio.ByteBuffer;

public class NativeBitMask<T extends Enum<T> & Flag> extends NativeDataType {

    public NativeBitMask(ByteBuffer byteBuffer, int offset) {
        super(byteBuffer, offset);
    }

    public final int get() {
        return getByteBuffer().getInt(getOffset());
    }

    @SafeVarargs
    public final void set(final T... flags) {
        getByteBuffer().putInt(getOffset(), get() | BitMask.of(flags));
    }

    public final boolean isSet(final T flag) {
        return BitMask.isSet(get(), flag);
    }

    public void clear() {
        getByteBuffer().putInt(getOffset(), 0);
    }

    @Override
    public String toString() {
        return super.toString() + " " + String.format("%16s", Integer.toBinaryString(get())).replace(' ', '0');
    }
}
