package de.hhu.bsinfo.neutrino.struct.field;

import de.hhu.bsinfo.neutrino.util.BitMask;
import de.hhu.bsinfo.neutrino.util.flag.ByteFlag;
import org.agrona.concurrent.AtomicBuffer;

public class NativeByteBitMask<T extends Enum<T> & ByteFlag> extends NativeDataType {

    public NativeByteBitMask(AtomicBuffer byteBuffer, final int offset) {
        super(byteBuffer, offset);
    }

    @Override
    public long getSize() {
        return Byte.BYTES;
    }

    public final byte get() {
        return getBuffer().getByte(getOffset());
    }

    @SafeVarargs
    public final void set(final T... flags) {
        getBuffer().putByte(getOffset(), (byte) (get() | BitMask.byteOf(flags)));
    }

    public final boolean isSet(final T flag) {
        return BitMask.isSet(get(), flag);
    }

    public void clear() {
        getBuffer().putByte(getOffset(), (byte) 0);
    }

    @Override
    public String toString() {
        return super.toString() + " " + String.format("%8s", Integer.toBinaryString(get())).replace(' ', '0');
    }
}
