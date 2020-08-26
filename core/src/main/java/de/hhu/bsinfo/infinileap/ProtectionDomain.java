package de.hhu.bsinfo.infinileap;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.MemoryAlignment;
import de.hhu.bsinfo.infinileap.util.MemoryUtil;
import de.hhu.bsinfo.infinileap.util.Struct;
import jdk.incubator.foreign.CSupport;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.linux.rdma.infinileap_h;
import org.linux.rdma.infinileap_h.*;

import static org.linux.rdma.infinileap_h.ibv_dealloc_pd;
import static org.linux.rdma.infinileap_h.ibv_reg_mr;

public class ProtectionDomain extends Struct {

    public ProtectionDomain() {
        super(ibv_pd.allocate());
    }

    public ProtectionDomain(MemoryAddress address) {
        super(address, ibv_pd.$LAYOUT());
    }

    public MemoryAddress getContext() {
        return ibv_pd.context$get(segment());
    }

    public int getHandle() {
        return ibv_pd.handle$get(segment());
    }

    public void setContext(final MemoryAddress value) {
        ibv_pd.context$set(segment(), value);
    }

    public void setHandle(final int value) {
        ibv_pd.handle$set(segment(), value);
    }

    public Buffer allocateMemory(long capacity, MemoryAlignment alignment, AccessFlag... accessFlags) {
        var segment = MemoryUtil.allocateMemory(capacity);
        var region = new MemoryRegion(ibv_reg_mr(this, segment, capacity, BitMask.intOf(accessFlags)));
        return new Buffer(segment, region);
    }

    @Override
    public void close() {
        ibv_dealloc_pd(this);
        super.close();
    }
}
