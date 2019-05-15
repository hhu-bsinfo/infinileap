package de.hhu.bsinfo.neutrino.data;

public class Result extends Struct {

    private static final int SIZE = Integer.BYTES + Long.BYTES;

    private final NativeInteger status = new NativeInteger(getByteBuffer(), 0);
    private final NativeLong resultHandle = new NativeLong(getByteBuffer(), 4);

    public Result() {
        super(SIZE);
    }

    public Result(long handle) {
        super(handle, SIZE);
    }

    public boolean isError() {
        return status.get() != 0;
    }

    public int getStatus() {
        return status.get();
    }

    public long getResultHandle() {
        return resultHandle.get();
    }

    @Override
    public String toString() {
        return "Result {\n" +
            "\tstatus=" + status +
            ",\n\tresultHandle=" + String.format("0x%016x", resultHandle.get()) +
            "\n}";
    }
}
