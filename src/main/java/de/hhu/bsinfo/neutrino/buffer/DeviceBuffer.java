package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.verbs.DeviceMemory;
import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;

public class DeviceBuffer extends RegisteredBuffer implements AutoCloseable {

    private DeviceMemory deviceMemory;

    public DeviceBuffer(DeviceMemory deviceMemory, MemoryRegion memoryRegion, long capacity) {
        super(memoryRegion, MemoryUtil.allocateMemory(capacity), capacity, null);

        this.deviceMemory = deviceMemory;
    }

    public boolean readFromDevice() {
        return deviceMemory.copyFromDeviceMemory(0, this, 0, capacity());
    }

    public boolean writeToDevice() {
        return deviceMemory.copyToDeviceMemory(this, 0, 0, capacity());
    }

    @Override
    public void close() {
        deviceMemory.close();
    }
}
