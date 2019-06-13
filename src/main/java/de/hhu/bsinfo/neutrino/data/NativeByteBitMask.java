package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.Flag;

public class NativeByteBitMask<T extends Enum<T> & Flag> extends NativeDataType {

    public NativeByteBitMask(LocalBuffer byteBuffer, final long offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Byte.BYTES;
    }

    public final byte get() {
        return getByteBuffer().get(getOffset());
    }

    @SafeVarargs
    public final void set(final T... flags) {
        getByteBuffer().put(getOffset(), (byte) (get() | BitMask.byteOf(flags)));
    }

    public final boolean isSet(final T flag) {
        return BitMask.isSet(get(), flag);
    }

    public void clear() {
        getByteBuffer().put(getOffset(), (byte) 0);
    }

    @Override
    public String toString() {
        return super.toString() + " " + String.format("%8s", Integer.toBinaryString(get())).replace(' ', '0');
    }
}
