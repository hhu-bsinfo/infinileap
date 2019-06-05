package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class NativeString extends NativeDataType {

    private final int length;

    private static final byte ZERO = 0;

    public NativeString(final LocalBuffer byteBuffer, final long offset, final int length) {
        super(byteBuffer, offset);
        this.length = length;
    }

    public void set(final String value) {
        byte[] valueBytes = value.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < length; i++) {
            if(i < valueBytes.length) {
                getByteBuffer().put(getOffset() + i, valueBytes[i]);
            } else {
                getByteBuffer().put(getOffset() + i, ZERO);
            }
        }
    }

    public String get() {
        byte[] ret = new byte[size()];
        for(int i = 0; i < size(); i++) {
            ret[i] = getByteBuffer().get(getOffset() + i);
        }

        return new String(ret, 0, length(), StandardCharsets.US_ASCII);
    }

    public int size() {
        return length;
    }

    private int length() {
        for(int i = 0; i < size(); i++) {
            if (getByteBuffer().get(i) == ZERO) {
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
