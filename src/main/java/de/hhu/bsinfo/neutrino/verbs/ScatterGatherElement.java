package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeArray;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Poolable;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import java.util.function.Consumer;

@LinkNative("ibv_sge")
public class ScatterGatherElement extends Struct implements Poolable {

    private final NativeLong address = longField("addr");
    private final NativeInteger length = integerField("length");
    private final NativeInteger localKey = integerField("lkey");

    private ScatterGatherElement(final long handle) {
        super(handle);
    }

    public ScatterGatherElement() {}

    public ScatterGatherElement(final Consumer<ScatterGatherElement> configurator) {
        configurator.accept(this);
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

    public static class Array extends NativeArray<ScatterGatherElement> {

        public Array(long handle, int capacity) {
            super(ScatterGatherElement::new, ScatterGatherElement.class, handle, capacity);
        }

        public Array(int capacity) {
            super(ScatterGatherElement::new, ScatterGatherElement.class, capacity);
        }
    }
}
