package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.*;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.Flag;
import de.hhu.bsinfo.neutrino.util.LinkNative;

@LinkNative("ibv_device_attr")
public class DeviceAttributes extends Struct {

    private final NativeString firmwareVersion = stringField("fw_ver", 64);
    private final NativeLong nodeGuid = longField("node_guid");
    private final NativeLong systemImageGuid = longField("sys_image_guid");
    private final NativeLong maxMemoryRegionSize = longField("max_mr_size");
    private final NativeLong pageSizeCapabilities = longField("page_size_cap");
    private final NativeInteger vendorId = integerField("vendor_id");
    private final NativeInteger vendorPartId = integerField("vendor_part_id");
    private final NativeInteger hardwareVersion = integerField("hw_ver");
    private final NativeInteger maxQueuePairCount = integerField("max_qp");
    private final NativeInteger maxQueuePairSize = integerField("max_qp_wr");
    private final NativeIntegerBitMask<CapabilityFlag> deviceCapabilities = integerBitField("device_cap_flags");
    private final NativeInteger maxScatterGatherCount = integerField("max_sge");
    private final NativeInteger maxRdScatterGatherCount = integerField("max_sge_rd");
    private final NativeInteger maxCompletionQueueCount = integerField("max_cq");
    private final NativeInteger maxCompletionQueueSize = integerField("max_cqe");
    private final NativeInteger maxMemoryRegionCount = integerField("max_mr");
    private final NativeInteger maxProtectionDomainCount = integerField("max_pd");
    private final NativeInteger maxAddressHandles = integerField("max_ah");
    private final NativeInteger maxSharedReceiveQueueCount = integerField("max_srq");
    private final NativeInteger maxSharedReceiveQueueSize = integerField("max_srq_wr");
    private final NativeInteger maxSharedReceiveQueueScatterGatherCount = integerField("max_srq_sge");
    private final NativeByte physicalPortCount = byteField("phys_port_cnt");

    DeviceAttributes() {}

    DeviceAttributes(LocalBuffer localBuffer, long offset) {
        super(localBuffer, offset);
    }

    public String getFirmwareVersion() {
        return firmwareVersion.get();
    }

    public long getNodeGuid() {
        return nodeGuid.get();
    }

    public long getSystemImageGuid() {
        return systemImageGuid.get();
    }

    public long getMaxMemoryRegionSize() {
        return maxMemoryRegionSize.get();
    }

    public long getPageSizeCapabilities() {
        return pageSizeCapabilities.get();
    }

    public int getVendorId() {
        return vendorId.get();
    }

    public int getVendorPartId() {
        return vendorPartId.get();
    }

    public int getHardwareVersion() {
        return hardwareVersion.get();
    }

    public int getMaxQueuePairCount() {
        return maxQueuePairCount.get();
    }

    public int getMaxQueuePairSize() {
        return maxQueuePairSize.get();
    }

    public int getDeviceCapabilities() {
        return deviceCapabilities.get();
    }

    public int getMaxScatterGatherCount() {
        return maxScatterGatherCount.get();
    }

    public int getMaxRdScatterGatherCount() {
        return maxRdScatterGatherCount.get();
    }

    public int getMaxCompletionQueueCount() {
        return maxCompletionQueueCount.get();
    }

    public int getMaxCompletionQueueSize() {
        return maxCompletionQueueSize.get();
    }

    public int getMaxMemoryRegionCount() {
        return maxMemoryRegionCount.get();
    }

    public int getMaxProtectionDomainCount() {
        return maxProtectionDomainCount.get();
    }

    public int getMaxAddressHandles() {
        return maxAddressHandles.get();
    }

    public int getMaxSharedReceiveQueueCount() {
        return maxSharedReceiveQueueCount.get();
    }

    public int getMaxSharedReceiveQueueSize() {
        return maxSharedReceiveQueueSize.get();
    }

    public int getMaxSharedReceiveQueueScatterGatherCount() {
        return maxSharedReceiveQueueScatterGatherCount.get();
    }

    public byte getPhysicalPortCount() {
        return physicalPortCount.get();
    }

    @Override
    public String toString() {
        return "DeviceAttributes {" +
            "\n\tfirmwareVersion=" + firmwareVersion +
            ",\n\tnodeGuid=" + nodeGuid +
            ",\n\tsystemImageGuid=" + systemImageGuid +
            ",\n\tmaxMemoryRegionSize=" + maxMemoryRegionSize +
            ",\n\tpageSizeCapabilities=" + pageSizeCapabilities +
            ",\n\tvendorId=" + vendorId +
            ",\n\tvendorPartId=" + vendorPartId +
            ",\n\thardwareVersion=" + hardwareVersion +
            ",\n\tmaxQueuePairCount=" + maxQueuePairCount +
            ",\n\tmaxQueuePairSize=" + maxQueuePairSize +
            ",\n\tdeviceCapabilities=" + deviceCapabilities +
            ",\n\tmaxScatterGatherCount=" + maxScatterGatherCount +
            ",\n\tmaxRdScatterGatherCount=" + maxRdScatterGatherCount +
            ",\n\tmaxCompletionQueueCount=" + maxCompletionQueueCount +
            ",\n\tmaxCompletionQueueSize=" + maxCompletionQueueSize +
            ",\n\tmaxMemoryRegionCount=" + maxMemoryRegionCount +
            ",\n\tmaxProtectionDomainCount=" + maxProtectionDomainCount +
            ",\n\tmaxAddressHandles=" + maxAddressHandles +
            ",\n\tmaxSharedReceiveQueueCount=" + maxSharedReceiveQueueCount +
            ",\n\tmaxSharedReceiveQueueSize=" + maxSharedReceiveQueueSize +
            ",\n\tmaxSharedReceiveQueueScatterGatherCount=" + maxSharedReceiveQueueScatterGatherCount +
            ",\n\tphysicalPortCount=" + physicalPortCount +
            "\n}";
    }

    public enum CapabilityFlag implements Flag {
        RESIZE_MAX_WR(1), BAD_PKEY_CNTR(1 <<  1), BAD_QKEY_CNTR(1 <<  2), RAW_MULTI(1 <<  3),
        AUTO_PATH_MIG(1 <<  4), CHANGE_PHY_PORT(1 <<  5), UD_AV_PORT_ENFORCE(1 <<  6), CURR_QP_STATE_MOD(1 <<  7),
        SHUTDOWN_PORT(1 <<  8), INIT_TYPE(1 <<  9), PORT_ACTIVE_EVENT(1 << 10), SYS_IMAGE_GUID(1 << 11),
        RC_RNR_NAK_GEN(1 << 12), SRQ_RESIZE(1 << 13), N_NOTIFY_CQ(1 << 14), MEM_WINDOW(1 << 17),
        UD_IP_CSUM(1 << 18), XRC(1 << 20), MEM_MGT_EXTENSIONS(1 << 21), MEM_WINDOW_TYPE_2A(1 << 23),
        MEM_WINDOW_TYPE_2B(1 << 24), RC_IP_CSUM(1 << 25), RAW_IP_CSUM(1 << 26), MANAGED_FLOW_STEERING(1 << 29),
        RAW_SCATTER_FCS(1L << 34), PCI_WRITE_END_PADDING(1L << 36);

        private final long value;

        CapabilityFlag(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }
}
