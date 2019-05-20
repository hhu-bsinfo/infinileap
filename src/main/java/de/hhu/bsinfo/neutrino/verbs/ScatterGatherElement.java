package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;

public class ScatterGatherElement extends Struct {

    private static final StructInformation INFO = StructUtil.getInfo("ibv_sge");

    public static final int SIZE = INFO.structSize.get();

    private final NativeLong address = new NativeLong(getByteBuffer(), INFO.getOffset("addr"));

    private final NativeInteger length = new NativeInteger(getByteBuffer(), INFO.getOffset("length"));

    private final NativeInteger localKey = new NativeInteger(getByteBuffer(), INFO.getOffset("lkey"));

    public ScatterGatherElement() {
        super(SIZE);
    }

    public ScatterGatherElement(final long handle) {
        super(handle, SIZE);
    }

    long getAddress() {
        return address.get();
    }

    int getLength() {
        return length.get();
    }

    int getLocalKey() {
        return localKey.get();
    }

    void setAddress(final long value) {
        address.set(value);
    }

    void setLength(final int value) {
        length.set(value);
    }

    void setLocalKey(final int value) {
        localKey.set(value);
    }
}
