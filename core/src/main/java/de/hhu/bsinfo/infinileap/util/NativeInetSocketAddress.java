package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.binding.NativeObject;
import de.hhu.bsinfo.infinileap.util.flag.ShortFlag;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;
import org.openucx.ucx_h.sockaddr_in;
import org.openucx.ucx_h.sockaddr_in6;
import org.openucx.ucx_h.sockaddr_storage;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.openucx.ucx_h.htons;

public class NativeInetSocketAddress extends NativeObject {

    /**
     * The address family.
     */
    private final AddressFamily family;

    /**
     * The address length in bytes.
     */
    private final int length;

    private NativeInetSocketAddress(AddressFamily family) {
        super(sockaddr_storage.allocate());

        this.family = family;
        sockaddr_storage.ss_family$set(segment(), family.getValue());

        switch (family) {
            case INET4 -> length = (int) sockaddr_in.sizeof();
            case INET6 -> length = (int) sockaddr_in6.sizeof();
            default    -> length = 0;
        }
    }

    private NativeInetSocketAddress setPort(short port) {
        var value = htons(port);
        switch (this.family) {
            case INET4 -> sockaddr_in.sin_port$set(segment(), value);
            case INET6 -> sockaddr_in6.sin6_port$set(segment(), value);
        }

        return this;
    }

    private NativeInetSocketAddress setScopeId(int scopeId) {
        if (this.family != AddressFamily.INET6) {
            throw new UnsupportedOperationException("Address not in INET6 family");
        }

        sockaddr_in6.sin6_scope_id$set(segment(), scopeId);
        return this;
    }

    private NativeInetSocketAddress setAddressBytes(byte[] bytes) {
        var source = MemorySegment.ofArray(bytes);
        switch (this.family) {
            case INET4 -> sockaddr_in.sin_addr$slice(segment()).copyFrom(source);
            case INET6 -> sockaddr_in6.sin6_addr$slice(segment()).copyFrom(source);
        }

        return this;
    }

    public static NativeInetSocketAddress convert(InetSocketAddress socketAddress) {
        var nativeAddress = new NativeInetSocketAddress(detectFamily(socketAddress));

        if (nativeAddress.family == AddressFamily.INET6) {
            nativeAddress.setScopeId(((Inet6Address) socketAddress.getAddress()).getScopeId());
        }

        return nativeAddress.setPort((short) socketAddress.getPort())
                .setAddressBytes(socketAddress.getAddress().getAddress());
    }

    private static AddressFamily detectFamily(InetSocketAddress socketAddress) {
        InetAddress address = socketAddress.getAddress();
        if (address instanceof Inet4Address) {
            return AddressFamily.INET4;
        } else if (address instanceof Inet6Address) {
            return AddressFamily.INET6;
        } else {
            throw new IllegalArgumentException("Unsupported address family");
        }
    }

    public AddressFamily getFamily() {
        return family;
    }

    public int getLength() {
        return length;
    }

    public enum AddressFamily implements ShortFlag {
        INET4((short) ucx_h.AF_INET()),
        INET6((short) ucx_h.AF_INET6());


        private final short value;

        AddressFamily(short value) {
            this.value = value;
        }

        @Override
        public short getValue() {
            return value;
        }
    }
}
