package de.hhu.bsinfo.neutrino.struct.field;

import org.agrona.concurrent.AtomicBuffer;

public class NativeEnum<T extends Enum<T>> extends NativeDataType {

    private final EnumConverter<T> converter;

    public NativeEnum(AtomicBuffer byteBuffer, final int offset, EnumConverter<T> converter) {
        super(byteBuffer, offset);
        this.converter = converter;
    }

    public final void set(final T value) {
        getBuffer().putInt(getOffset(), converter.toInt(value));
    }

    public final T get() {
        return converter.toEnum(getBuffer().getInt(getOffset()));
    }

    @Override
    public long getSize() {
        return Integer.BYTES;
    }

    @Override
    public String toString() {
        return super.toString() + " " + get().name();
    }
}
