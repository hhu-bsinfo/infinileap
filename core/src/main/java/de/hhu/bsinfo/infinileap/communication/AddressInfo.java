package de.hhu.bsinfo.infinileap.communication;

import static org.linux.rdma.infinileap_h.*;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import jdk.incubator.foreign.MemoryAddress;
import org.linux.rdma.infinileap_h;

public final class AddressInfo extends NativeObject {

    public AddressInfo() {
        super(rdma_addrinfo.allocate());
    }

    public AddressInfo(MemoryAddress address) {
        super(address, rdma_addrinfo.$LAYOUT());
    }

    public int getFlags() {
        return rdma_addrinfo.ai_flags$get(segment());
    }

    public int getFamily() {
        return rdma_addrinfo.ai_family$get(segment());
    }

    public int getQueuePairType() {
        return rdma_addrinfo.ai_qp_type$get(segment());
    }

    public int getPortSpace() {
        return rdma_addrinfo.ai_port_space$get(segment());
    }

    public int getSourceLength() {
        return rdma_addrinfo.ai_src_len$get(segment());
    }

    public int getDestinationLength() {
        return rdma_addrinfo.ai_dst_len$get(segment());
    }

    public MemoryAddress getSourceAddress() {
        return rdma_addrinfo.ai_src_addr$get(segment());
    }

    public MemoryAddress getDestinationAddress() {
        return rdma_addrinfo.ai_dst_addr$get(segment());
    }

    public MemoryAddress getSourceCanonicalName() {
        return rdma_addrinfo.ai_src_canonname$get(segment());
    }

    public MemoryAddress getDestinationCanonicalName() {
        return rdma_addrinfo.ai_dst_canonname$get(segment());
    }

    public long getRouteLength() {
        return rdma_addrinfo.ai_route_len$get(segment());
    }

    public MemoryAddress getRoute() {
        return rdma_addrinfo.ai_route$get(segment());
    }

    public long getConnectionLength() {
        return rdma_addrinfo.ai_connect_len$get(segment());
    }

    public MemoryAddress getConnection() {
        return rdma_addrinfo.ai_connect$get(segment());
    }

    public MemoryAddress getNext() {
        return rdma_addrinfo.ai_next$get(segment());
    }

    public void setFlags(final Flag... flags) {
        rdma_addrinfo.ai_flags$set(segment(), BitMask.intOf(flags));
    }

    public void setFamily(final int value) {
        rdma_addrinfo.ai_family$set(segment(), value);
    }

    public void setQueuePairType(final int value) {
        rdma_addrinfo.ai_qp_type$set(segment(), value);
    }

    public void setPortSpace(final PortSpace portSpace) {
        rdma_addrinfo.ai_port_space$set(segment(), portSpace.getValue());
    }

    public void setSourceLength(final int value) {
        rdma_addrinfo.ai_src_len$set(segment(), value);
    }

    public void setDestinationLength(final int value) {
        rdma_addrinfo.ai_dst_len$set(segment(), value);
    }

    public void setSourceAddress(final MemoryAddress value) {
        rdma_addrinfo.ai_src_addr$set(segment(), value);
    }

    public void setDestinationAddress(final MemoryAddress value) {
        rdma_addrinfo.ai_dst_addr$set(segment(), value);
    }

    public void setSourceCanonicalName(final MemoryAddress value) {
        rdma_addrinfo.ai_src_canonname$set(segment(), value);
    }

    public void setDestinationCanonicalName(final MemoryAddress value) {
        rdma_addrinfo.ai_dst_canonname$set(segment(), value);
    }

    public void setRouteLength(final long value) {
        rdma_addrinfo.ai_route_len$set(segment(), value);
    }

    public void setRoute(final MemoryAddress value) {
        rdma_addrinfo.ai_route$set(segment(), value);
    }

    public void setConnectionLength(final long value) {
        rdma_addrinfo.ai_connect_len$set(segment(), value);
    }

    public void setConnection(final MemoryAddress value) {
        rdma_addrinfo.ai_connect$set(segment(), value);
    }

    public void setNext(final MemoryAddress value) {
        rdma_addrinfo.ai_next$set(segment(), value);
    }

    @Override
    public void close() {
        rdma_freeaddrinfo(this);
        super.close();
    }

    public enum Flag implements IntegerFlag {
        PASSIVE(infinileap_h.RAI_PASSIVE()), NUMERICHOST(infinileap_h.RAI_NUMERICHOST()),
        NOROUTE(infinileap_h.RAI_NOROUTE()), FAMILY(infinileap_h.RAI_FAMILY());

        private final int value;

        Flag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
