package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.Poolable;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Result implements NativeObject, Poolable {

    private static final int SIZE = Integer.BYTES + Long.BYTES;

    private final NativeInteger status;
    private final NativeLong value;
    private final long handle;

    public Result() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        handle = MemoryUtil.getAddress(byteBuffer);
        status = new NativeInteger(byteBuffer, 0);
        value = new NativeLong(byteBuffer, 4);
    }

    public Result(long handle) {
        ByteBuffer byteBuffer = MemoryUtil.wrap(handle, SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        this.handle = handle;
        status = new NativeInteger(byteBuffer, 0);
        value = new NativeLong(byteBuffer, 4);
    }

    public boolean isError() {
        return status.get() != 0;
    }

    public int getStatus() {
        return status.get();
    }

    public <T extends NativeObject> T get(ReferenceFactory<T> factory) {
        return factory.newInstance(value.get());
    }

    public long longValue() {
        return value.get();
    }

    public int intValue() {
        return (int) value.get();
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public String toString() {
        return "Result {\n" +
            "\tstatus=" + status +
            ",\n\tvalue=" + String.format("0x%016x", value.get()) +
            "\n}";
    }
}
