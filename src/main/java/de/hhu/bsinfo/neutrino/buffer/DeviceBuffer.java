package de.hhu.bsinfo.neutrino.buffer;

import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import de.hhu.bsinfo.neutrino.util.UnsafeProvider;
import de.hhu.bsinfo.neutrino.verbs.DeviceMemory;
import de.hhu.bsinfo.neutrino.verbs.MemoryRegion;

import java.io.IOException;

public class DeviceBuffer extends RegisteredBuffer implements AutoCloseable {

    private DeviceMemory deviceMemory;

    public DeviceBuffer(DeviceMemory deviceMemory, MemoryRegion memoryRegion, long capacity) {
        super(memoryRegion, UnsafeProvider.getUnsafe().allocateMemory(capacity), capacity, null);

        this.deviceMemory = deviceMemory;
    }

    public void readFromDevice() throws IOException {
        deviceMemory.copyFromDeviceMemory(0, this, 0, capacity());
    }

    public void writeToDevice() throws IOException {
        deviceMemory.copyToDeviceMemory(this, 0, 0, capacity());
    }

    @Override
    public void close() throws IOException {
        deviceMemory.close();
        super.close();
    }
}
