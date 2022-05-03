package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.network.NativeInetSocketAddress;
import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.ValueLayout;
import org.openucx.ucp_conn_request_attr_t;
import org.unix.sockaddr_in;
import org.unix.sockaddr_in6;
import org.unix.sockaddr_storage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static org.openucx.OpenUcx.*;
import static org.unix.Linux.ntohs;

public class ConnectionRequest {

    private final MemoryAddress address;

    private final long data;

    private ConnectionRequest(MemoryAddress address, long data) {
        this.address = address;
        this.data = data;
    }

    MemoryAddress address() {
        return address;
    }

    public long getData() {
        return data;
    }

    public long getClientIdentifier() throws ControlException {
        var attributes = queryAttributes(Field.CLIENT_ID);
        return ucp_conn_request_attr_t.client_id$get(attributes);
    }

    public InetSocketAddress getClientAddress() throws ControlException {

        // Extract client address and address family from attributes
        var attributes = queryAttributes(Field.CLIENT_ADDR);
        var clientAddress = ucp_conn_request_attr_t.client_address$slice(attributes);
        var addressFamily = NativeInetSocketAddress.AddressFamily.of(
                sockaddr_storage.ss_family$get(clientAddress)
        );

        // Get raw address bytes
        var addressBytes = switch (addressFamily) {
            case INET4 -> sockaddr_in.sin_addr$slice(clientAddress).toArray(ValueLayout.OfByte.JAVA_BYTE);
            case INET6 -> sockaddr_in6.sin6_addr$slice(clientAddress).toArray(ValueLayout.OfByte.JAVA_BYTE);
        };

        // Convert port to host byte order
        var port = switch (addressFamily) {
            case INET4 -> Short.toUnsignedInt(ntohs(sockaddr_in.sin_port$get(clientAddress)));
            case INET6 -> Short.toUnsignedInt(ntohs(sockaddr_in6.sin6_port$get(clientAddress)));
        };

        try {
            var inetAddress = InetAddress.getByAddress(addressBytes);
            return new InetSocketAddress(inetAddress, port);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private MemorySegment queryAttributes(Field... fields) throws ControlException {
        // Allocate attributes struct and set requested fields
        var request_attr = ucp_conn_request_attr_t.allocate(ResourceScope.newImplicitScope());
        ucp_conn_request_attr_t.field_mask$set(request_attr, BitMask.longOf(fields));

        // Query requested fields
        var status = ucp_conn_request_query(address, request_attr);
        if (Status.isNot(status, Status.OK)) {
            throw new ControlException(status);
        }

        return request_attr;
    }

    static ConnectionRequest of(MemoryAddress address, long data) {
        return new ConnectionRequest(address, data);
    }

    public enum Field implements LongFlag {
        CLIENT_ADDR(UCP_CONN_REQUEST_ATTR_FIELD_CLIENT_ADDR()),
        CLIENT_ID(UCP_CONN_REQUEST_ATTR_FIELD_CLIENT_ID());

        private final long value;

        Field(int value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }
}
