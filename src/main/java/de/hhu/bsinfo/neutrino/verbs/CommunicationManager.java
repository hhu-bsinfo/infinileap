package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.Result;
import de.hhu.bsinfo.neutrino.util.NativeError;
import de.hhu.bsinfo.neutrino.util.SystemUtil;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class CommunicationManager {

    private CommunicationManager() {}

    public static AddressInfo getAddressInfo(InetSocketAddress socketAddress, AddressInfo hints) throws IOException {
        var result = Result.localInstance();
        var host = socketAddress.getAddress().isAnyLocalAddress() ? null : socketAddress.getAddress().getHostAddress();
        var port = String.valueOf(socketAddress.getPort());

        getAddressInfo0(
                host,
                port,
                hints.getHandle(),
                result.getHandle()
        );

        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        return result.get(AddressInfo::new);
    }

    public static Endpoint createEndpoint(AddressInfo addressInfo, @Nullable ProtectionDomain protectionDomain, QueuePair.InitialAttributes attributes) throws IOException {
        var result = Result.localInstance();

        createEndpoint0(
            addressInfo.getHandle(),
            protectionDomain != null ? protectionDomain.getHandle() : 0,
            attributes.getHandle(),
            result.getHandle()
        );

        if (result.isError()) {
            throw new IOException(SystemUtil.getErrorMessage());
        }

        return result.get(Endpoint::new);
    }

    static native void getAddressInfo0(String node, String service, long hints, long result);

    static native void freeAddressInfo0(long addressInfo, long result);

    static native void createEndpoint0(long addressInfo, long protectionDomain, long initialAttributes, long result);

    static native void destroyEndpoint0(long identifier, long result);

    static native void listen0(long identifier, int backlog, long result);

    static native void getRequest0(long serverIdentifier, long result);

    static native void accept0(long clientIdentifier, long parameters, long result);

    static native void reject0(long clientIdentifier, long privateData, byte dataLength, long result);

    static native void connect0(long serverIdentifier, long parameters, long result);

    static native void disconnect0(long identifier, long result);
}
