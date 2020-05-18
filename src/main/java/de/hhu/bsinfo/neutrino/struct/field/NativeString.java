package de.hhu.bsinfo.neutrino.struct.field;

import org.agrona.concurrent.AtomicBuffer;

import java.nio.charset.StandardCharsets;

public class NativeString extends NativeDataType {

    /**
     * The native string's maximum length.
     */
    private final int length;

    private static final byte ZERO = 0;

    public NativeString(final AtomicBuffer byteBuffer, final int offset, final int length) {
        super(byteBuffer, offset);
        this.length = length;
    }

    public void set(final String value) {
        byte[] valueBytes = value.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < length; i++) {
            if(i < valueBytes.length) {
                getBuffer().putByte(getOffset() + i, valueBytes[i]);
            } else {
                getBuffer().putByte(getOffset() + i, ZERO);
            }
        }
    }

    public String get() {
        byte[] ret = new byte[size()];
        for(int i = 0; i < size(); i++) {
            ret[i] = getBuffer().getByte(getOffset() + i);
        }

        return new String(ret, 0, length(), StandardCharsets.US_ASCII);
    }

    public int size() {
        return length;
    }

    private int length() {
        for(int i = 0; i < size(); i++) {
            if (getBuffer().getByte(getOffset() + i) == ZERO) {
                return i;
            }
        }

        return length;
    }

    @Override
    public long getSize() {
        return length;
    }

    @Override
    public String toString() {
        return super.toString() + " " + get();
    }
}
