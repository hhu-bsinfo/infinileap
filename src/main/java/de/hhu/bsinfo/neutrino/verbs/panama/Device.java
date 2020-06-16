package de.hhu.bsinfo.neutrino.verbs.panama;

import de.hhu.bsinfo.neutrino.verbs.panama.util.Struct;
import jdk.incubator.foreign.MemoryAddress;
import org.linux.rdma.Cstring;
import org.linux.rdma.ibverbs_h.*;

public class Device extends Struct {

    public Device() {
        super(Cibv_device::allocate);
    }

    public Device(MemoryAddress address) {
        super(Cibv_device.$LAYOUT(), address);
    }

    public String name() {
        return Cstring.toJavaStringRestricted(Cibv_device.name$addr(memoryAddress()));
    }

    public NodeType nodeType() {
        return NodeType.CONVERTER.toEnum(Cibv_device.node_type$get(memoryAddress()));
    }

    public TransportType transportType() {
        return TransportType.CONVERTER.toEnum(Cibv_device.transport_type$get(memoryAddress()));
    }
}
