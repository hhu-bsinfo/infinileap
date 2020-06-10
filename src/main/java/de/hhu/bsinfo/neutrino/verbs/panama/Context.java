package de.hhu.bsinfo.neutrino.verbs.panama;

import jdk.incubator.foreign.CSupport;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import org.linux.rdma.Cint;
import org.linux.rdma.Clong;
import org.linux.rdma.Cpointer;
import org.linux.rdma.Cstring;

import java.io.IOException;

import static org.linux.rdma.ibverbs_h.*;

@Slf4j
public final class Context implements NativeObject {

    private static final long SIZE = Cibv_context.sizeof();

    private final MemoryAddress memoryAddress;

    private Context(MemoryAddress memoryAddress) {
        this.memoryAddress = memoryAddress;
    }

    public static Context openDevice(int index) throws IOException {
        try (var segment = Cint.allocate(0)) {

            // Query device list
            var numDevices = segment.baseAddress();
            var deviceList = ibv_get_device_list(numDevices);

            // Check if the returned list is valid
            if (deviceList.equals(MemoryAddress.NULL)) {
                throw new IOException("querying device list failed");
            }

            var deviceCount = Cint.get(numDevices);

            log.info("Found {} InfiniBand devices", deviceCount);

            // Check if the specified device is available
            if (index >= deviceCount) {
                ibv_free_device_list(deviceList);
                throw new IOException("device does not exist");
            }

            var deviceArray = Cpointer.asArrayRestricted(deviceList, deviceCount);
            var deviceAddress = MemorySegment.ofNativeRestricted(
                    Cpointer.get(deviceArray, index),
                    Cibv_device.sizeof(),
                    Thread.currentThread(),
                    null,
                    null).baseAddress();

            log.info("Using device {}", Cstring.toJavaStringRestricted(Cibv_device.name$addr(deviceAddress)));

            // Open device at the specified index
            var contextAddress = ibv_open_device(deviceAddress);
            ibv_free_device_list(deviceList);
            deviceAddress.segment().close();

            // Check if the returned device is valid
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


    @Override
    public MemoryAddress memoryAddress() {
        return memoryAddress;
    }

    @Override
    public long sizeOf() {
        return SIZE;
    }

    @Override
    public void close() {
        memoryAddress.segment().close();
    }
}
