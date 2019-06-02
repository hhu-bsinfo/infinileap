package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.verbs.DeviceMemory;
import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;

public class DeviceBuffer extends LocalBuffer implements AutoCloseable {

    private DeviceMemory deviceMemory;
    private MemoryRegion memoryRegion;

    public DeviceBuffer(DeviceMemory deviceMemory, MemoryRegion memoryRegion, long capacity) {
        super(MemoryUtil.allocateMemory(capacity), capacity);

        this.deviceMemory = deviceMemory;
        this.memoryRegion = memoryRegion;
    }

    public DeviceMemory getDeviceMemory() {
        return deviceMemory;
    }

    public MemoryRegion getMemoryRegion() {
        return memoryRegion;
    }

    public boolean readFromDevice() {
        return deviceMemory.copyFromDeviceMemory(0, this, 0, capacity());
    }

    public boolean writeToDevice() {
        return deviceMemory.copyToDeviceMemory(this, 0, 0, capacity());
    }

    @Override
    public void close() throws Exception {
        deviceMemory.close();
    }
}
