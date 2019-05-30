package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import de.hhu.bsinfo.neutrino.util.UnsafeProvider;
import java.lang.ref.Cleaner;
import java.nio.BufferOverflowException;
import java.nio.ByteOrder;

public final class LocalBuffer implements NativeObject {

    @SuppressWarnings("UseOfSunClasses")
    private static final sun.misc.Unsafe UNSAFE = UnsafeProvider.getUnsafe();

    private static final long ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    private static final Cleaner CLEANER = Cleaner.create();
    private static final Object FAKE_PARENT = new Object();

    private final long handle;
    private final long capacity;

    @SuppressWarnings("FieldCanBeLocal")
    private final Object parent;

    private LocalBuffer(long handle, long capacity, Object parent) {
        this.handle = handle;
        this.capacity = capacity;
        this.parent = parent;
        if (this.parent == null) {
            CLEANER.register(this, new Destructor(handle));
        }
    }

    // TODO(krakowski)
    //  Perform bound checks within each method

    public byte get(long index) {
        return UNSAFE.getByte(index);
    }

    public short getShort(long index) {
        return UNSAFE.getShort(index);
    }

    public int getInt(long index) {
        return UNSAFE.getInt(index);
    }

    public long getLong(long index) {
        return UNSAFE.getLong(index);
    }

    public char getChar(long index) {
        return UNSAFE.getChar(index);
    }

    public float getFloat(long index) {
        return UNSAFE.getFloat(index);
    }

    public double getDouble(long index) {
        return UNSAFE.getDouble(index);
    }

    public <T extends NativeObject> T getObject(long index, ReferenceFactory<T> factory) {
        return factory.newInstance(handle + index);
    }

    public void getBuffer(long index, LocalBuffer target, long offset, long length) {
        UNSAFE.copyMemory(handle + index, target.handle + offset, length);
    }

    public void get(long index, byte[] target, int offset, int length) {
        UNSAFE.copyMemory(null, handle + index, target, ARRAY_BASE_OFFSET + offset, length);
    }

    public void put(long index, byte value) {
        UNSAFE.putByte(index, value);
    }

    public void putShort(long index, short value) {
        UNSAFE.putShort(index, value);
    }

    public void putInt(long index, int value) {
        UNSAFE.putInt(index, value);
    }

    public void putLong(long index, long value) {
        UNSAFE.putLong(index, value);
    }

    public void putChar(long index, char value) {
        UNSAFE.putChar(index, value);
    }

    public void putFloat(long index, float value) {
        UNSAFE.putFloat(index, value);
    }

    public void putDouble(long index, double value) {
        UNSAFE.putDouble(index, value);
    }

    public void putObject(long index, NativeObject object) {
        UNSAFE.copyMemory(object.getHandle(), handle + index, object.getNativeSize());
    }

    public void putBuffer(long index, LocalBuffer source, long offset, long length) {
        UNSAFE.copyMemory(source.handle + offset, handle + index, length);
    }

    public void put(long index, byte[] source, int offset, int length) {
        UNSAFE.copyMemory(source, ARRAY_BASE_OFFSET + offset, null, handle + index, length);
    }

    public LocalBuffer slice(long index, long length) {
        if (index + length > capacity) {
            throw new BufferOverflowException();
        }

        long sliceHandle = handle + index;
        return new LocalBuffer(sliceHandle, length, this);
    }

    public boolean isDirect() {
        return true;
    }

    public boolean isReadOnly() {
        return false;
    }

    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public long getNativeSize() {
        return capacity;
    }

    public static LocalBuffer allocate(long capacity) {
        return new LocalBuffer(UNSAFE.allocateMemory(capacity), capacity, null);
    }

    public static LocalBuffer wrap(long handle, long capacity) {
        return new LocalBuffer(handle, capacity, FAKE_PARENT);
    }

    private static final class Destructor implements Runnable {

        private final long handle;

        private Destructor(long handle) {
            this.handle = handle;
        }

        @Override
        public void run() {
            UNSAFE.freeMemory(handle);
        }
    }
}
