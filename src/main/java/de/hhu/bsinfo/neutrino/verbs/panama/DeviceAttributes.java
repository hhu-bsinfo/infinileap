package de.hhu.bsinfo.neutrino.verbs.panama;

import static org.linux.rdma.ibverbs_h.*;

import de.hhu.bsinfo.neutrino.util.flag.IntegerFlag;
import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.CSupport;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public final class DeviceAttributes extends Struct {

    public DeviceAttributes() {
        super(ibv_device_attr.allocate());
    }

    public DeviceAttributes(MemoryAddress address) {
        super(address, ibv_device_attr.$LAYOUT());
    }

    public String getFirmwareVersion() {
        return CSupport.toJavaString(ibv_device_attr.fw_ver$slice(segment()));
    }

    public long getNodeGuid() {
        return ibv_device_attr.node_guid$get(segment());
    }

    public long getSystemImageGuid() {
        return ibv_device_attr.sys_image_guid$get(segment());
    }

    public long getMaxMemoryRegionSize() {
        return ibv_device_attr.max_mr_size$get(segment());
    }

    public long getPageSizeCapabilities() {
        return ibv_device_attr.page_size_cap$get(segment());
    }

    public int getVendorId() {
        return ibv_device_attr.vendor_id$get(segment());
    }

    public int getVendorPartId() {
        return ibv_device_attr.vendor_part_id$get(segment());
    }

    public int getHardwareVersion() {
        return ibv_device_attr.hw_ver$get(segment());
    }

    public int getMaxQueuePairCount() {
        return ibv_device_attr.max_qp$get(segment());
    }

    public int getMaxWorkRequestCount() {
        return ibv_device_attr.max_qp_wr$get(segment());
    }

    public int getDeviceCapabilityFlags() {
        return ibv_device_attr.device_cap_flags$get(segment());
    }

    public int getMaxScatterGatherElements() {
        return ibv_device_attr.max_sge$get(segment());
    }

    public int getMaxRdmaScatterGatherCount() {
        return ibv_device_attr.max_sge_rd$get(segment());
    }

    public int getMaxCompletionQueueCount() {
        return ibv_device_attr.max_cq$get(segment());
    }

    public int getMaxCompletionQueueCapacity() {
        return ibv_device_attr.max_cqe$get(segment());
    }

    public int getMaxMemoryRegionCount() {
        return ibv_device_attr.max_mr$get(segment());
    }

    public int getMaxProtectionDomainCount() {
        return ibv_device_attr.max_pd$get(segment());
    }

    public int getMaxQueuePairAtomicOperations() {
        return ibv_device_attr.max_qp_rd_atom$get(segment());
    }

    public int getMaxEndToEndAtomicOperations() {
        return ibv_device_attr.max_ee_rd_atom$get(segment());
    }

    public int getMaxRdmaResources() {
        return ibv_device_attr.max_res_rd_atom$get(segment());
    }

    public int getMaxRdmaQueuePairDepth() {
        return ibv_device_attr.max_qp_init_rd_atom$get(segment());
    }

    public int getMaxEndToEndQueuePairDepth() {
        return ibv_device_attr.max_ee_init_rd_atom$get(segment());
    }

    public int getAtomicCapabilities() {
        return ibv_device_attr.atomic_cap$get(segment());
    }

    public int getMaxEndToEndCount() {
        return ibv_device_attr.max_ee$get(segment());
    }

    public int getMaxReliableDatagramDomains() {
        return ibv_device_attr.max_rdd$get(segment());
    }

    public int getMaxMemoryWindows() {
        return ibv_device_attr.max_mw$get(segment());
    }

    public int getMaxRawIpv6QueuePairs() {
        return ibv_device_attr.max_raw_ipv6_qp$get(segment());
    }

    public int getMaxRawEthernetQueuePairs() {
        return ibv_device_attr.max_raw_ethy_qp$get(segment());
    }

    public int getMaxMulticastGroups() {
        return ibv_device_attr.max_mcast_grp$get(segment());
    }

    public int getMaxQueuePairsPerMulticastGroup() {
        return ibv_device_attr.max_mcast_qp_attach$get(segment());
    }

    public int getMaxMulticastGroupQueuePairs() {
        return ibv_device_attr.max_total_mcast_qp_attach$get(segment());
    }

    public int getMaxAddressHandles() {
        return ibv_device_attr.max_ah$get(segment());
    }

    public int getMaxFastMemoryRegistrations() {
        return ibv_device_attr.max_fmr$get(segment());
    }

    public int getMaxMapPerFastMemoryRegistration() {
        return ibv_device_attr.max_map_per_fmr$get(segment());
    }

    public int getMaxSharedReceiveQueue() {
        return ibv_device_attr.max_srq$get(segment());
    }

    public int getMaxSharedReceiveQueueWorkRequests() {
        return ibv_device_attr.max_srq_wr$get(segment());
    }

    public int getMaxSharedReceiveQueueScatterGatherElements() {
        return ibv_device_attr.max_srq_sge$get(segment());
    }

    public short getMaxPartitions() {
        return ibv_device_attr.max_pkeys$get(segment());
    }

    public byte getLocalCaAckDelay() {
        return ibv_device_attr.local_ca_ack_delay$get(segment());
    }

    public byte getPhysicalPortCount() {
        return ibv_device_attr.phys_port_cnt$get(segment());
    }

    public void setNodeGuid(final long value) {
        ibv_device_attr.node_guid$set(segment(), value);
    }

    public void setSystemImageGuid(final long value) {
        ibv_device_attr.sys_image_guid$set(segment(), value);
    }

    public void setMaxMemoryRegionSize(final long value) {
        ibv_device_attr.max_mr_size$set(segment(), value);
    }

    public void setPageSizeCapabilities(final long value) {
        ibv_device_attr.page_size_cap$set(segment(), value);
    }

    public void setVendorId(final int value) {
        ibv_device_attr.vendor_id$set(segment(), value);
    }

    public void setVendorPartId(final int value) {
        ibv_device_attr.vendor_part_id$set(segment(), value);
    }

    public void setHardwareVersion(final int value) {
        ibv_device_attr.hw_ver$set(segment(), value);
    }

    public void setMaxQueuePairCount(final int value) {
        ibv_device_attr.max_qp$set(segment(), value);
    }

    public void setMaxWorkRequestCount(final int value) {
        ibv_device_attr.max_qp_wr$set(segment(), value);
    }

    public void setDeviceCapabilityFlags(final int value) {
        ibv_device_attr.device_cap_flags$set(segment(), value);
    }

    public void setMaxScatterGatherElements(final int value) {
        ibv_device_attr.max_sge$set(segment(), value);
    }

    public void setMaxRdmaScatterGatherCount(final int value) {
        ibv_device_attr.max_sge_rd$set(segment(), value);
    }

    public void setMaxCompletionQueueCount(final int value) {
        ibv_device_attr.max_cq$set(segment(), value);
    }

    public void setMaxCompletionQueueCapacity(final int value) {
        ibv_device_attr.max_cqe$set(segment(), value);
    }

    public void setMaxMemoryRegionCount(final int value) {
        ibv_device_attr.max_mr$set(segment(), value);
    }

    public void setMaxProtectionDomainCount(final int value) {
        ibv_device_attr.max_pd$set(segment(), value);
    }

    public void setMaxQueuePairAtomicOperations(final int value) {
        ibv_device_attr.max_qp_rd_atom$set(segment(), value);
    }

    public void setMaxEndToEndAtomicOperations(final int value) {
        ibv_device_attr.max_ee_rd_atom$set(segment(), value);
    }

    public void setMaxRdmaResources(final int value) {
        ibv_device_attr.max_res_rd_atom$set(segment(), value);
    }

    public void setMaxRdmaQueuePairDepth(final int value) {
        ibv_device_attr.max_qp_init_rd_atom$set(segment(), value);
    }

    public void setMaxEndToEndQueuePairDepth(final int value) {
        ibv_device_attr.max_ee_init_rd_atom$set(segment(), value);
    }

    public void setAtomicCapabilities(final int value) {
        ibv_device_attr.atomic_cap$set(segment(), value);
    }

    public void setMaxEndToEndCount(final int value) {
        ibv_device_attr.max_ee$set(segment(), value);
    }

    public void setMaxReliableDatagramDomains(final int value) {
        ibv_device_attr.max_rdd$set(segment(), value);
    }

    public void setMaxMemoryWindows(final int value) {
        ibv_device_attr.max_mw$set(segment(), value);
    }

    public void setMaxRawIpv6QueuePairs(final int value) {
        ibv_device_attr.max_raw_ipv6_qp$set(segment(), value);
    }

    public void setMaxRawEthernetQueuePairs(final int value) {
        ibv_device_attr.max_raw_ethy_qp$set(segment(), value);
    }

    public void setMaxMulticastGroups(final int value) {
        ibv_device_attr.max_mcast_grp$set(segment(), value);
    }

    public void setMaxQueuePairsPerMulticastGroup(final int value) {
        ibv_device_attr.max_mcast_qp_attach$set(segment(), value);
    }

    public void setMaxMulticastGroupQueuePairs(final int value) {
        ibv_device_attr.max_total_mcast_qp_attach$set(segment(), value);
    }

    public void setMaxAddressHandles(final int value) {
        ibv_device_attr.max_ah$set(segment(), value);
    }

    public void setMaxFastMemoryRegistrations(final int value) {
        ibv_device_attr.max_fmr$set(segment(), value);
    }

    public void setMaxMapPerFastMemoryRegistration(final int value) {
        ibv_device_attr.max_map_per_fmr$set(segment(), value);
    }

    public void setMaxSharedReceiveQueue(final int value) {
        ibv_device_attr.max_srq$set(segment(), value);
    }

    public void setMaxSharedReceiveQueueWorkRequests(final int value) {
        ibv_device_attr.max_srq_wr$set(segment(), value);
    }

    public void setMaxSharedReceiveQueueScatterGatherElements(final int value) {
        ibv_device_attr.max_srq_sge$set(segment(), value);
    }

    public void setMaxPartitions(final short value) {
        ibv_device_attr.max_pkeys$set(segment(), value);
    }

    public void setLocalCaAckDelay(final byte value) {
        ibv_device_attr.local_ca_ack_delay$set(segment(), value);
    }

    public void setPhysicalPortCount(final byte value) {
        ibv_device_attr.phys_port_cnt$set(segment(), value);
    }

    public enum CapabilityFlag implements IntegerFlag {
        RESIZE_MAX_WR(IBV_DEVICE_RESIZE_MAX_WR()), BAD_PKEY_CNTR(IBV_DEVICE_BAD_PKEY_CNTR()),
        BAD_QKEY_CNTR(IBV_DEVICE_BAD_QKEY_CNTR()), RAW_MULTI(IBV_DEVICE_RAW_MULTI()),
        AUTO_PATH_MIG(IBV_DEVICE_AUTO_PATH_MIG()), CHANGE_PHY_PORT(IBV_DEVICE_CHANGE_PHY_PORT()),
        UD_AV_PORT_ENFORCE(IBV_DEVICE_UD_AV_PORT_ENFORCE()), CURR_QP_STATE_MOD(IBV_DEVICE_CURR_QP_STATE_MOD()),
        SHUTDOWN_PORT(IBV_DEVICE_SHUTDOWN_PORT()), INIT_TYPE(IBV_DEVICE_INIT_TYPE()),
        PORT_ACTIVE_EVENT(IBV_DEVICE_PORT_ACTIVE_EVENT()), SYS_IMAGE_GUID(IBV_DEVICE_SYS_IMAGE_GUID()),
        RC_RNR_NAK_GEN(IBV_DEVICE_RC_RNR_NAK_GEN()), SRQ_RESIZE(IBV_DEVICE_SRQ_RESIZE()),
        N_NOTIFY_CQ(IBV_DEVICE_N_NOTIFY_CQ()), MEM_WINDOW(IBV_DEVICE_MEM_WINDOW()),
        UD_IP_CSUM(IBV_DEVICE_UD_IP_CSUM()), XRC(IBV_DEVICE_XRC()), MEM_MGT_EXTENSIONS(IBV_DEVICE_MEM_MGT_EXTENSIONS()),
        MEM_WINDOW_TYPE_2A(IBV_DEVICE_MEM_WINDOW_TYPE_2A()), MEM_WINDOW_TYPE_2B(IBV_DEVICE_MEM_WINDOW_TYPE_2B()),
        RC_IP_CSUM(IBV_DEVICE_RC_IP_CSUM()), RAW_IP_CSUM(IBV_DEVICE_RAW_IP_CSUM()),
        MANAGED_FLOW_STEERING(IBV_DEVICE_MANAGED_FLOW_STEERING());

        private final int value;

        CapabilityFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
