package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.field.NativeLong;
import de.hhu.bsinfo.neutrino.struct.field.NativeObject;
import de.hhu.bsinfo.neutrino.util.MemoryAlignment;
import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.Poolable;
import de.hhu.bsinfo.neutrino.util.factory.ReferenceFactory;
import org.agrona.concurrent.AtomicBuffer;

public class Result implements NativeObject, Poolable {

    private static final ThreadLocal<Result> LOCAL_RESULT = ThreadLocal.withInitial(Result::new);

    private static final int SIZE = Integer.BYTES + Long.BYTES;

    private final NativeInteger status;
    private final NativeLong value;
    private final long handle;

    static native String getErrorMessage(int errorNumber);

    public Result() {
        AtomicBuffer byteBuffer = MemoryUtil.allocateAligned(SIZE, MemoryAlignment.CACHE);
        handle = byteBuffer.addressOffset();
        status = new NativeInteger(byteBuffer, 0);
        value = new NativeLong(byteBuffer, 4);
    }

    public Result(long handle) {
        AtomicBuffer byteBuffer = MemoryUtil.wrap(handle, SIZE);
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

    public String getStatusMessage() {
        int statusNumber = status.get();

        if(statusNumber == 0) {
            return "OK";
        } else if(statusNumber == -1) {
            return "Unknown error";
        } else {
            return getErrorMessage(statusNumber);
        }
    }

    public <T extends NativeObject> T get(ReferenceFactory<T> factory) {
        var handle = value.get();
        return handle == 0 ? null : factory.newInstance(handle);
    }

    public long longValue() {
        return value.get();
    }

    public int intValue() {
        return (int) value.get();
    }

    public short shortValue() {
        return (short) value.get();
    }

    public byte byteValue() {
        return (byte) value.get();
    }

    public static Result localInstance() {
        return LOCAL_RESULT.get();
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public int getNativeSize() {
        return SIZE;
    }

    @Override
    public String toString() {
        return "Result {\n" +
            "\tstatus=" + status +
            ",\n\tvalue=" + String.format("0x%016x", value.get()) +
            "\n}";
    }
}
