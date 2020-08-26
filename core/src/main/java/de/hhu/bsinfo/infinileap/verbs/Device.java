package de.hhu.bsinfo.infinileap.verbs;

import de.hhu.bsinfo.infinileap.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import lombok.extern.slf4j.Slf4j;
import org.linux.rdma.infinileap_h.ibv_device;

import static jdk.incubator.foreign.CSupport.toJavaStringRestricted;

@Slf4j
public class Device extends Struct {

    public Device() {
        super(ibv_device.allocate());
    }

    public Device(MemoryAddress address) {
        super(address, ibv_device.$LAYOUT());
    }

    public String name() {
        return toJavaStringRestricted(ibv_device.name$slice(segment()).address());
    }

    public NodeType nodeType() {
        return NodeType.CONVERTER.toEnum(ibv_device.node_type$get(segment()));
    }

    public TransportType transportType() {
        return TransportType.CONVERTER.toEnum(ibv_device.transport_type$get(segment()));
    }
}
