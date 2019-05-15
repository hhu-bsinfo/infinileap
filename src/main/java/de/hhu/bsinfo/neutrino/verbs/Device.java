package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeString;
import de.hhu.bsinfo.neutrino.data.Struct;
import de.hhu.bsinfo.neutrino.data.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;

public class Device extends Struct {

    private static final StructInformation info = StructUtil.getDeviceAttribtues();

    private static final int SIZE = info.structSize.get();

    private final NativeString firmwareVersion = new NativeString(getByteBuffer(), info.getOffset("fw_ver"), 64);
    private final NativeLong nodeGuid = new NativeLong(getByteBuffer(), info.getOffset("node_guid"));
    private final NativeLong systemImageGuid = new NativeLong(getByteBuffer(), info.getOffset("sys_image_guid"));
    private final NativeLong maxMemoryRegionSize = new NativeLong(getByteBuffer(), info.getOffset("max_mr_size"));
    private final NativeLong pageSizeCapabilities = new NativeLong(getByteBuffer(), info.getOffset("page_size_cap"));
    private final NativeInteger vendorId = new NativeInteger(getByteBuffer(), info.getOffset("vendor_id"));
    private final NativeInteger vendorPartId = new NativeInteger(getByteBuffer(), info.getOffset("vendor_part_id"));
    private final NativeInteger hardwareVersion = new NativeInteger(getByteBuffer(), info.getOffset("hw_ver"));
    private final NativeInteger maxQueuePairCount = new NativeInteger(getByteBuffer(), info.getOffset("max_qp"));
    private final NativeInteger maxQueuePairSize = new NativeInteger(getByteBuffer(), info.getOffset("max_qp_wr"));
    private final NativeInteger deviceCapabilities = new NativeInteger(getByteBuffer(), info.getOffset("device_cap_flags"));
    private final NativeInteger maxScatterGatherCount = new NativeInteger(getByteBuffer(), info.getOffset("max_sge"));
    private final NativeInteger maxRdScatterGatherCount = new NativeInteger(getByteBuffer(), info.getOffset("max_sge_rd"));
    private final NativeInteger maxCompletionQueueCount = new NativeInteger(getByteBuffer(), info.getOffset("max_cq"));
    private final NativeInteger maxCompletionQueueSize = new NativeInteger(getByteBuffer(), info.getOffset("max_cqe"));
    private final NativeInteger maxMemoryRegionCount = new NativeInteger(getByteBuffer(), info.getOffset("max_mr"));
    private final NativeInteger maxProtectionDomainCount = new NativeInteger(getByteBuffer(), info.getOffset("max_pd"));
    private final NativeInteger maxAddressHandles = new NativeInteger(getByteBuffer(), info.getOffset("max_ah"));
    private final NativeInteger maxSharedReceiveQueueCount = new NativeInteger(getByteBuffer(), info.getOffset("max_srq"));
    private final NativeInteger maxSharedReceiveQueueSize = new NativeInteger(getByteBuffer(), info.getOffset("max_srq_wr"));
    private final NativeInteger maxSharedReceiveQueueScatterGatherCount = new NativeInteger(getByteBuffer(), info.getOffset("max_srq_sge"));
    private final NativeByte physicalPortCount = new NativeByte(getByteBuffer(), info.getOffset("phys_port_cnt"));

    public static int getDeviceCount() {
        return Verbs.getNumDevices();
    }

    Device() {
        super(SIZE);
    }

    Device(long handle) {
        super(handle, SIZE);
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
