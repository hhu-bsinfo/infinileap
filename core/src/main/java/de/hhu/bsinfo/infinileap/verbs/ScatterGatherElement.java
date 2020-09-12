package de.hhu.bsinfo.infinileap.verbs;

import static org.linux.rdma.infinileap_h.*;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;

public final class ScatterGatherElement extends NativeObject {

    public ScatterGatherElement() {
        super(ibv_sge.allocate());
    }

    public ScatterGatherElement(MemoryAddress address) {
        super(address, ibv_sge.$LAYOUT());
    }

    public long getMemoryAddress() {
        return ibv_sge.addr$get(segment());
    }

    public int getLength() {
        return ibv_sge.length$get(segment());
    }

    public int getLocalKey() {
        return ibv_sge.lkey$get(segment());
    }

    public void setMemoryAddress(final long value) {
        ibv_sge.addr$set(segment(), value);
    }

    public void setLength(final int value) {
        ibv_sge.length$set(segment(), value);
    }

    public void setLocalKey(final int value) {
        ibv_sge.lkey$set(segment(), value);
    }
}
