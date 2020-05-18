package de.hhu.bsinfo.neutrino.struct.field;

public interface NativeObject {

    long NULL = 0L;

    /**
     * The native object's virtual memory address.
     */
    long getHandle();

    /**
     * The native object's size in bytes.
     */
    int getNativeSize();
}
