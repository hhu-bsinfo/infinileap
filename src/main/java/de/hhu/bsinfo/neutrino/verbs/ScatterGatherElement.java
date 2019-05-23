package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Poolable;

@LinkNative("ibv_sge")
public class ScatterGatherElement extends Struct implements Poolable {

    private final NativeLong address = longField("addr");
    private final NativeInteger length = integerField("length");
    private final NativeInteger localKey = integerField("lkey");

    public ScatterGatherElement() {
    }

    public ScatterGatherElement(final long handle) {
        super(handle);
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
