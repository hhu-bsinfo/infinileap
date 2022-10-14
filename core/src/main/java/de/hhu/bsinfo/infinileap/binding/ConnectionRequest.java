package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.network.NativeInetSocketAddress;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

import org.openucx.ucp_conn_request_attr_t;

import java.net.InetSocketAddress;

import static org.openucx.OpenUcx.*;

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

        // Convert native address struct to InetSocketAddress
        var nativeAddress = NativeInetSocketAddress.wrap(clientAddress);
        return nativeAddress.toInetSocketAddress();
    }

    private MemorySegment queryAttributes(Field... fields) throws ControlException {
        // Allocate attributes struct and set requested fields
        var request_attr = ucp_conn_request_attr_t.allocate(MemorySession.openImplicit());
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
