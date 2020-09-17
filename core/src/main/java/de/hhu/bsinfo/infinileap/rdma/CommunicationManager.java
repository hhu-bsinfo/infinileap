package de.hhu.bsinfo.infinileap.rdma;

import de.hhu.bsinfo.infinileap.util.Status;
import de.hhu.bsinfo.infinileap.verbs.ProtectionDomain;
import de.hhu.bsinfo.infinileap.verbs.QueuePair;
import jdk.incubator.foreign.CSupport;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

import static org.linux.rdma.infinileap_h.rdma_create_ep;
import static org.linux.rdma.infinileap_h.rdma_getaddrinfo;

@Slf4j
public class CommunicationManager {

    public static CommunicationIdentifier createEndpoint(AddressInfo addressInfo) {
        return createEndpoint(addressInfo, null, null);
    }

    public static CommunicationIdentifier createEndpoint(AddressInfo addressInfo, @Nullable ProtectionDomain protectionDomain, @Nullable QueuePair.InitialAttributes attributes) {
        try (var pointer = MemorySegment.allocateNative(CSupport.C_POINTER)) {
            var status = rdma_create_ep(
                    pointer,
                    addressInfo,
                    protectionDomain == null ? MemoryAddress.NULL : protectionDomain,
                    attributes == null ? MemoryAddress.NULL : attributes
            );

            if (status != Status.OK) {
                throw new RuntimeException(Status.getErrorMessage());
            }

            return new CommunicationIdentifier(MemoryAccess.getAddress(pointer));
        }
    }

    public static AddressInfo getAddressInfo(InetSocketAddress socketAddress, AddressInfo hints) {
        try (var result = MemorySegment.allocateNative(CSupport.C_POINTER);
             var node = CSupport.toCString(socketAddress.getAddress().getHostAddress());
             var service = CSupport.toCString(String.valueOf(socketAddress.getPort()))) {

             var address = socketAddress.getAddress();
             var status = rdma_getaddrinfo(
                     address.isAnyLocalAddress() ? MemoryAddress.NULL : node,
                     service,
                     hints,
                     result
             );

            if (status != Status.OK) {
                throw new RuntimeException(Status.getErrorMessage());
            }

             return new AddressInfo(MemoryAccess.getAddress(result));
        }
    }
}
