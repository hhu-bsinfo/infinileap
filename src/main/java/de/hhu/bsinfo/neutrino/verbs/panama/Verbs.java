package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.verbs.panama.util.CloseableList;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.linux.rdma.Cint;

import java.io.IOException;

import static org.linux.rdma.ibverbs_h.*;

public class Verbs {

    public static CloseableList<Device> queryDevices() throws IOException {
        try (var segment = Cint.allocate(0)) {
            var numDevicesAddress = segment.baseAddress();
            var deviceListAddress = ibv_get_device_list(numDevicesAddress);
            if (deviceListAddress.equals(MemoryAddress.NULL)) {
                throw new IOException("querying device list failed");
            }

            return DeviceList.ofNativeRestricted(deviceListAddress, Cint.get(numDevicesAddress));
        }
    }

    public static Context openDevice(Device device) throws IOException {
        var contextAddress = ibv_open_device(device.memoryAddress());
        if (contextAddress.equals(MemoryAddress.NULL)) {
            throw new IOException("device not found");
        }

        return new Context(MemorySegment.ofNativeRestricted(
                contextAddress,
                Cibv_context.sizeof(),
                Thread.currentThread(),
                null,
                null).baseAddress());
    }
}
