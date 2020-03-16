package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.util.Poolable;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;

public class Result implements NativeObject, Poolable {

    private static final ThreadLocal<Result> LOCAL_RESULT = ThreadLocal.withInitial(Result::new);

    private static final int SIZE = Integer.BYTES + Long.BYTES;

    private final NativeInteger status;
    private final NativeLong value;
    private final long handle;

    static native String getErrorMessage(int errorNumber);

    public Result() {
        var byteBuffer = LocalBuffer.allocate(SIZE);
        handle = byteBuffer.getHandle();
        status = new NativeInteger(byteBuffer, 0);
        value = new NativeLong(byteBuffer, 4);
    }

    public Result(long handle) {
        var byteBuffer = LocalBuffer.wrap(handle, SIZE);
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
        return factory.newInstance(value.get());
    }

    public <T extends NativeObject> T getAndRelease(ReferenceFactory<T> factory) {
        long tmp = value.get();
        releaseInstance();
        return tmp == 0 ? null : factory.newInstance(tmp);
    }

    public long getLongAndRelease() {
        long ret = longValue();
        releaseInstance();
        return ret;
    }

    public int getIntAndRelease() {
        int ret = intValue();
        releaseInstance();
        return ret;
    }

    public short getShortAndRelease() {
        short ret = shortValue();
        releaseInstance();
        return ret;
    }

    public byte getByteAndRelease() {
        byte ret = byteValue();
        releaseInstance();
        return ret;
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
    public long getNativeSize() {
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
