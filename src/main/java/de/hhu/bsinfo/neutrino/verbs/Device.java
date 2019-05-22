package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeString;
import de.hhu.bsinfo.neutrino.struct.Struct;

public class Device extends Struct {

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
    private final NativeInteger deviceCapabilities = integerField("device_cap_flags");
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

    Device() {
        super("ibv_device_attr");
    }

    Device(long handle) {
        super("ibv_device_attr", handle);
    }

    public static int getDeviceCount() {
        return Verbs.getNumDevices();
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
        return "Device {" +
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
            ",\n\tmaxSharedReceiveQueueScatterGatherCount="
            + maxSharedReceiveQueueScatterGatherCount +
            ",\n\tphysicalPortCount=" + physicalPortCount +
            "\n}";
    }
}
