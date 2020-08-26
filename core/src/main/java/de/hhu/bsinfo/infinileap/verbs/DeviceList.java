package de.hhu.bsinfo.infinileap.verbs;

import de.hhu.bsinfo.infinileap.util.CloseableList;
import jdk.incubator.foreign.CSupport;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import org.linux.rdma.RuntimeHelper;

import java.io.IOException;
import java.util.ArrayList;

import static org.linux.rdma.infinileap_h.ibv_free_device_list;

@Slf4j
public class DeviceList extends ArrayList<Device> implements CloseableList<Device> {

    private final MemorySegment segment;

    public DeviceList(MemoryAddress memoryAddress, int length) {
        segment = RuntimeHelper.asArrayRestricted(memoryAddress, CSupport.C_POINTER, length);
    }

    @Override
    public void close() throws IOException {
        for (var resource : this) {
            resource.close();
        }

        ibv_free_device_list(segment);
        segment.close();
    }

    public static DeviceList ofNativeRestricted(MemoryAddress address, int length) {
        var list = new DeviceList(address, length);
        for (int i = 0; i < length; i++) {
            list.add(new Device(MemoryAccess.getAddressAtIndex(list.segment, i)));
        }

        return list;
    }
}
