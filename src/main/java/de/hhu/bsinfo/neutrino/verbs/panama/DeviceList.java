package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.verbs.panama.util.CloseableList;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.linux.rdma.Cpointer;

import java.io.IOException;
import java.util.ArrayList;

import static org.linux.rdma.ibverbs_h.ibv_free_device_list;

public class DeviceList extends ArrayList<Device> implements CloseableList<Device> {

    private final MemorySegment segment;

    private final MemoryAddress baseAddress;

    public DeviceList(MemoryAddress memoryAddress, int length) {
        baseAddress = Cpointer.asArrayRestricted(memoryAddress, length);
        segment = baseAddress.segment();
    }

    @Override
    public void close() throws IOException {
        for (var resource : this) {
            resource.close();
        }

        ibv_free_device_list(baseAddress);
        segment.close();
    }

    public static DeviceList ofNativeRestricted(MemoryAddress address, int length) {
        var list = new DeviceList(address, length);
        for (int i = 0; i < length; i++) {
            list.add(new Device(Cpointer.get(list.baseAddress, i)));
        }

        return list;
    }
}
