package de.hhu.bsinfo.infinileap;

import static org.linux.rdma.infinileap_h.*;

import de.hhu.bsinfo.infinileap.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import org.linux.rdma.infinileap_h;

public final class MemoryRegion extends Struct {

    public MemoryRegion() {
        super(ibv_mr.allocate());
    }

    public MemoryRegion(MemoryAddress address) {
        super(address, ibv_mr.$LAYOUT());
    }

    public MemoryAddress getContext() {
        return ibv_mr.context$get(segment());
    }

    public MemoryAddress getProtectionDomain() {
        return ibv_mr.pd$get(segment());
    }

    public MemoryAddress getMemoryRegionAddress() {
        return ibv_mr.addr$get(segment());
    }

    public long getLength() {
        return ibv_mr.length$get(segment());
    }

    public int getHandle() {
        return ibv_mr.handle$get(segment());
    }

    public int getLocalKey() {
        return ibv_mr.lkey$get(segment());
    }

    public int getRemoteKey() {
        return ibv_mr.rkey$get(segment());
    }

    public void setContext(final MemoryAddress value) {
        ibv_mr.context$set(segment(), value);
    }

    public void setProtectionDomain(final MemoryAddress value) {
        ibv_mr.pd$set(segment(), value);
    }

    public void setMemoryRegionAddress(final MemoryAddress value) {
        ibv_mr.addr$set(segment(), value);
    }

    public void setLength(final long value) {
        ibv_mr.length$set(segment(), value);
    }

    public void setHandle(final int value) {
        ibv_mr.handle$set(segment(), value);
    }

    public void setLocalKey(final int value) {
        ibv_mr.lkey$set(segment(), value);
    }

    public void setRemoteKey(final int value) {
        ibv_mr.rkey$set(segment(), value);
    }

    @Override
    public void close() {
        ibv_dereg_mr(this);
        super.close();
    }
}
