package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import de.hhu.bsinfo.neutrino.verbs.AccessFlag;
import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;
import de.hhu.bsinfo.neutrino.verbs.ProtectionDomain;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public final class LocalByteBuffer implements NativeObject {

    private final MemoryRegion localRegion;
    private final ByteBuffer byteBuffer;
    private final long handle;

    private LocalByteBuffer(ByteBuffer byteBuffer, MemoryRegion localRegion, long handle) {
        this.byteBuffer = byteBuffer;
        this.localRegion = localRegion;
        this.handle = handle;
    }

    public byte get(int index) {
        return byteBuffer.get(index);
    }

    public short getShort(int index) {
        return byteBuffer.getShort(index);
    }

    public int getInt(int index) {
        return byteBuffer.getInt(index);
    }

    public long getLong(int index) {
        return byteBuffer.getLong(index);
    }

    public char getChar(int index) {
        return byteBuffer.getChar(index);
    }

    public float getFloat(int index) {
        return byteBuffer.getFloat(index);
    }

    public double getDouble(int index) {
        return byteBuffer.getDouble(index);
    }

    public <T extends NativeObject> T getObject(int index, ReferenceFactory<T> factory) {
        return factory.newInstance(handle + index);
    }

    public void put(int index, byte value) {
        byteBuffer.put(index, value);
    }

    public void putShort(int index, short value) {
        byteBuffer.putShort(index, value);
    }

    public void putInt(int index, int value) {
        byteBuffer.putInt(index, value);
    }

    public void putLong(int index, long value) {
        byteBuffer.putLong(index, value);
    }

    public void putChar(int index, char value) {
        byteBuffer.putChar(index, value);
    }

    public void putFloat(int index, float value) {
        byteBuffer.putFloat(index, value);
    }

    public void putDouble(int index, double value) {
        byteBuffer.putDouble(index, value);
    }

    public int capacity() {
        return byteBuffer.capacity();
    }
    
    public int getLocalKey() {
        return localRegion.getLocalKey();
    }
    
    public int getRemoteKey() {
        return localRegion.getRemoteKey();
    }
    
    public void read(RemoteByteBuffer remoteBuffer) {
        remoteBuffer.read(this);
    }
    
    public void write(RemoteByteBuffer remoteBuffer) {
        remoteBuffer.write(this);
    }

    // TODO
    //  Implement absolute bulk methods once we switched to JDK 13

    public void free() {
        localRegion.deregister();
    }

    public LocalByteBuffer slice(int index, int length) {
        if (index + length > byteBuffer.capacity()) {
            throw new BufferOverflowException();
        }

        long sliceHandle = handle + index;
        ByteBuffer buffer = MemoryUtil.wrap(sliceHandle, length);
        return new LocalByteBuffer(buffer, localRegion, sliceHandle);
    }

    public static LocalByteBuffer allocate(ProtectionDomain protectionDomain, int size) {
        var buffer = ByteBuffer.allocateDirect(size);
        var region = protectionDomain.registerMemoryRegion(buffer, AccessFlag.LOCAL_WRITE, AccessFlag.REMOTE_READ, AccessFlag.REMOTE_WRITE);
        return new LocalByteBuffer(buffer, region, region.getAddress());
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public String toString() {
        return localRegion.toString();
    }
}
