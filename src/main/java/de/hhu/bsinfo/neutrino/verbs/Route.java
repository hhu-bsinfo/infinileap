package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;

@LinkNative("rdma_route")
public final class Route extends Struct {

    private final NativeLong address = longField("addr");
    private final NativeLong pathRec = longField("path_rec");
    private final NativeInteger numPaths = integerField("num_paths");

    Route() {}

    Route(long handle) {
        super(handle);
    }

    Route(final LocalBuffer buffer, final long offset) {
        super(buffer, offset);
    }

    public long getAddress() {
        return address.get();
    }

    public long getPathRec() {
        return pathRec.get();
    }

    public int getNumPaths() {
        return numPaths.get();
    }

    public void setAddress(final long value) {
        address.set(value);
    }

    public void setPathRec(final long value) {
        pathRec.set(value);
    }

    public void setNumPaths(final int value) {
        numPaths.set(value);
    }
}