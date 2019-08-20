package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import de.hhu.bsinfo.neutrino.util.UnsafeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Cleaner;
import java.nio.ByteOrder;

public class LocalBuffer implements NativeObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalBuffer.class);

    @SuppressWarnings("UseOfSunClasses")
    private static final sun.misc.Unsafe UNSAFE = UnsafeProvider.getUnsafe();
    private static final long ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
    private static final Cleaner CLEANER = Cleaner.create();
    protected static final Object FAKE_PARENT = new Object();
    private static final byte ZERO = 0;

    private long handle;
    private long capacity;

    @SuppressWarnings("FieldCanBeLocal")
    private Object parent;

    protected LocalBuffer(long handle, long capacity) {
        this(handle, capacity, null);
    }

    protected LocalBuffer(long handle, long capacity, Object parent) {
        this.handle = handle;
        this.capacity = capacity;
        this.parent = parent;
        if (this.parent == null) {
            CLEANER.register(this, new Destructor(handle));
        }
    }

    public byte get(long index) {
        checkBounds(index, index + Byte.BYTES);
        return UNSAFE.getByte(handle + index);
    }

    public short getShort(long index) {
        checkBounds(index, index + Short.BYTES);
        return UNSAFE.getShort(handle + index);
    }

    public int getInt(long index) {
        checkBounds(index, index + Integer.BYTES);
        return UNSAFE.getInt(handle + index);
    }

    public long getLong(long index) {
        checkBounds(index, index + Long.BYTES);
        return UNSAFE.getLong(handle + index);
    }

    public char getChar(long index) {
        checkBounds(index, index + Character.BYTES);
        return UNSAFE.getChar(handle + index);
    }

    public float getFloat(long index) {
        checkBounds(index, index + Float.BYTES);
        return UNSAFE.getFloat(handle + index);
    }

    public double getDouble(long index) {
        checkBounds(index, index + Double.BYTES);
        return UNSAFE.getDouble(handle + index);
    }

    public <T extends NativeObject> T getObject(long index, ReferenceFactory<T> factory) {
        var object = factory.newInstance(handle + index);
        checkBounds(index, index + object.getNativeSize());
        return object;
    }

    public void getBuffer(long index, LocalBuffer target, long offset, long length) {
        target.checkBounds(offset, offset + length);
        checkBounds(index, index + length);
        UNSAFE.copyMemory(handle + index, target.handle + offset, length);
    }

    public void get(long index, byte[] target, int offset, int length) {
        checkArrayBounds(target, offset, offset + length);
        checkBounds(index, index + length);
        UNSAFE.copyMemory(null, handle + index, target, ARRAY_BASE_OFFSET + offset, length);
    }

    public void put(long index, byte value) {
        checkBounds(index, index + Byte.BYTES);
        UNSAFE.putByte(handle + index, value);
    }

    public void putShort(long index, short value) {
        checkBounds(index, index + Byte.BYTES);
        UNSAFE.putShort(handle + index, value);
    }

    public void putInt(long index, int value) {
        checkBounds(index, index + Byte.BYTES);
        UNSAFE.putInt(handle + index, value);
    }

    public void putLong(long index, long value) {
        checkBounds(index, index + Byte.BYTES);
        UNSAFE.putLong(handle + index, value);
    }

    public void putChar(long index, char value) {
        checkBounds(index, index + Byte.BYTES);
        UNSAFE.putChar(handle + index, value);
    }

    public void putFloat(long index, float value) {
        checkBounds(index, index + Byte.BYTES);
        UNSAFE.putFloat(handle + index, value);
    }

    public void putDouble(long index, double value) {
        checkBounds(index, index + Byte.BYTES);
        UNSAFE.putDouble(handle + index, value);
    }

    public void putObject(NativeObject object) {
        putObject(0, object);
    }

    public void putObject(long index, NativeObject object) {
        checkBounds(index, index + object.getNativeSize());
        UNSAFE.copyMemory(object.getHandle(), handle + index, object.getNativeSize());
    }

    public void putBuffer(LocalBuffer source) {
        putBuffer(0, source, 0, source.capacity());
    }

    public void putBuffer(long index, LocalBuffer source, long offset, long length) {
        source.checkBounds(offset, offset + length);
        checkBounds(index, index + length);
        UNSAFE.copyMemory(source.handle + offset, handle + index, length);
    }

    public void put(long index, byte[] source, int offset, int length) {
        checkArrayBounds(source, offset, offset + length);
        checkBounds(index, index + length);
        UNSAFE.copyMemory(source, ARRAY_BASE_OFFSET + offset, null, handle + index, length);
    }

    public LocalBuffer slice(long index, long length) {
        checkBounds(index, index + length);
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

    public long capacity() {
        return capacity;
    }

    public void clear() {
        UNSAFE.setMemory(handle, capacity, ZERO);
    }

    /**
     * Checks if the specified indices are within the accessible memory range.
     */
    private void checkBounds(long indexFrom, long indexTo) {
        if (indexFrom >= capacity || indexFrom < 0 || indexTo > capacity || indexTo < 0 || indexFrom > indexTo) {
            throw new IndexOutOfBoundsException(String.format("Range [%d,%d] is not accessible", indexFrom, indexTo));
        }
    }

    /**
     * Checks if the specified indices are within the specified array's bounds.
     */
    private void checkArrayBounds(byte[] array, long indexFrom, long indexTo) {
        if (indexFrom >= array.length || indexFrom < 0 || indexTo > array.length || indexTo < 0 || indexFrom > indexTo) {
            throw new IndexOutOfBoundsException(String.format("Range [%d,%d] is not accessible", indexFrom, indexTo));
        }
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public long getNativeSize() {
        return capacity;
    }

    public void reWrap(long handle, long capacity) {
        if(parent != FAKE_PARENT) {
            throw new UnsupportedOperationException("Calling reWrap() is only allowed on buffers, which have been created using wrap()");
        }

        this.handle = handle;
        this.capacity = capacity;
    }

    public static LocalBuffer allocate(long capacity) {
        var handle = UNSAFE.allocateMemory(capacity);
        UNSAFE.setMemory(handle, capacity, ZERO);
        LOGGER.trace("Allocated memory at {}", String.format("%016X", handle));
        return new LocalBuffer(handle, capacity);
    }

    public static LocalBuffer wrap(long handle, long capacity) {
        return new LocalBuffer(handle, capacity, FAKE_PARENT);
    }

    @Override
    public String toString() {
        return "LocalBuffer {\n" +
                "\taddress=" + handle +
                ",\n\tcapacity=" + capacity +
                "\n}";
    }

    private static final class Destructor implements Runnable {

        private final long handle;

        private Destructor(long handle) {
            this.handle = handle;
        }

        @Override
        public void run() {
            if(handle == ZERO) {
                return;
            }

            UNSAFE.freeMemory(handle);
            LOGGER.trace("Freed {}", String.format("%016X", handle));
        }
    }
}
