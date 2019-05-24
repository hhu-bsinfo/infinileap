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

    public long getAddress() {
        return address.get();
    }

    public int getLength() {
        return length.get();
    }

    public int getLocalKey() {
        return localKey.get();
    }

    public void setAddress(final long value) {
        address.set(value);
    }

    public void setLength(final int value) {
        length.set(value);
    }

    public void setLocalKey(final int value) {
        localKey.set(value);
    }
}
