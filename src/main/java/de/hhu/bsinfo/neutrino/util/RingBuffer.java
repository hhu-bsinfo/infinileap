package de.hhu.bsinfo.neutrino.util;

import java.util.Collection;

public class RingBuffer<T> {

    public static final int REMOVE_SIGN = 0x7FFFFFFF;

    private final T[] buffer;

    private int posBack;
    private int posFront;

    private static int getUnsignedInt(final int number) {
        return number & REMOVE_SIGN;
    }


    @SuppressWarnings("unchecked")
    public RingBuffer(final int size) {
        buffer = (T[]) new Object[size];
    }

    public int size() {
        return buffer.length;
    }

    public void clear() {
        for(int i = 0; i< buffer.length; i++) {
            buffer[i] = null;
        }

        posBack = 0;
        posFront = 0;
    }

    public boolean isEmpty() {
        return getUnsignedInt(posBack) == getUnsignedInt(posFront);
    }

    public boolean isFull() {
        return getUnsignedInt(posFront) == getUnsignedInt(posBack) + buffer.length;
    }

    public void push(final T object) {
        if(!isFull()) {
            buffer[getUnsignedInt(posFront) % buffer.length] = object;
            posFront++;
        }
    }

    public T pop() {
        if(isEmpty()) {
            return null;
        }

        T ret = buffer[getUnsignedInt(posBack) % buffer.length];
        posBack++;

        return ret;
    }
}
