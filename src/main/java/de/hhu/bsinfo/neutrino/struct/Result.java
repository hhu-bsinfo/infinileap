package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeObject;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;

public class Result extends Struct {

    private static final int SIZE = Integer.BYTES + Long.BYTES;

    private final NativeInteger status = new NativeInteger(getByteBuffer(), 0);
    private final NativeLong pointer = new NativeLong(getByteBuffer(), 4);

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

    public <T extends NativeObject> T get(ReferenceFactory<T> factory) {
        return factory.newInstance(pointer.get());
    }

    public long getPointer() {
        return pointer.get();
    }

    @Override
    public String toString() {
        return "Result {\n" +
            "\tstatus=" + status +
            ",\n\tpointer=" + String.format("0x%016x", pointer.get()) +
            "\n}";
    }
}
