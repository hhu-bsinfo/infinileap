package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Result implements NativeObject {

    private static final int SIZE = Integer.BYTES + Long.BYTES;

    private final NativeInteger status;
    private final NativeLong pointer;
    private final long handle;

    public Result() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        handle = MemoryUtil.getAddress(byteBuffer);
        status = new NativeInteger(byteBuffer, 0);
        pointer = new NativeLong(byteBuffer, 4);
    }

    public Result(long handle) {
        ByteBuffer byteBuffer = MemoryUtil.wrap(handle, SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        this.handle = handle;
        status = new NativeInteger(byteBuffer, 0);
        pointer = new NativeLong(byteBuffer, 4);
    }

    public boolean isError() {
        return status.get() != 0;
    }

    public int getStatus() {
        return status.get();
    }

    public <T extends NativeObject> T get(ReferenceFactory<T> factory) {
        return factory.newInstance(pointer.get());
    }

    public long getPointer() {
        return pointer.get();
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public String toString() {
        return "Result {\n" +
            "\tstatus=" + status +
            ",\n\tpointer=" + String.format("0x%016x", pointer.get()) +
            "\n}";
    }
}
