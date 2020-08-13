package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.verbs.panama.util.CloseableList;
import jdk.incubator.foreign.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static org.linux.rdma.ibverbs_h.ibv_get_device_list;
import static org.linux.rdma.ibverbs_h.ibv_open_device;

@Slf4j
public class Verbs {

    public static final byte FALSE = 0;
    public static final byte TRUE = 1;

    public static CloseableList<Device> queryDevices() throws IOException {
        try (var numDevices = MemorySegment.allocateNative(CSupport.C_INT)) {
            var deviceListAddress = ibv_get_device_list(numDevices);
            if (deviceListAddress.equals(MemoryAddress.NULL)) {
                throw new IOException("querying device list failed");
            }

            return DeviceList.ofNativeRestricted(deviceListAddress, MemoryAccess.getInt(numDevices));
        }
    }

    public static Context openDevice(Device device) throws IOException {
        var contextAddress = ibv_open_device(device);
        if (contextAddress.equals(MemoryAddress.NULL)) {
            throw new IOException("device not found");
        }

        return new Context(contextAddress);
    }

    public static byte toByte(boolean value) {
        if (value) {
            return 1;
        }

        return 0;
    }
}
