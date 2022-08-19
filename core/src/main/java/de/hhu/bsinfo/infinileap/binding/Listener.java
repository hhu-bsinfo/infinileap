package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.network.NativeInetSocketAddress;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;
import org.openucx.ucp_listener_attr_t;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.net.InetSocketAddress;

import static org.openucx.OpenUcx.*;

public class Listener extends NativeObject implements AutoCloseable {

    private final ListenerParameters parameters;

    /* package-private */ Listener(MemoryAddress address, ListenerParameters parameters) {
        super(address, ValueLayout.ADDRESS);
        this.parameters = parameters;
    }

    public void reject(ConnectionRequest connectionRequest) throws ControlException {
        var status = ucp_listener_reject(
                Parameter.of(this),
                connectionRequest.address()
        );

        if (Status.isNot(status, Status.OK)) {
            throw new ControlException(status);
        }
    }

    public InetSocketAddress getAddress() throws ControlException {
        // Extract client address and address family from attributes
        var attributes = queryAttributes(Field.SOCKADDR);
        var listenerAddress = ucp_listener_attr_t.sockaddr$slice(attributes);

        // Convert native address struct to InetSocketAddress
        var nativeAddress = NativeInetSocketAddress.wrap(listenerAddress);
        return nativeAddress.toInetSocketAddress();
    }

    @Override
    public void close() {
        ucp_listener_destroy(address());
    }

    private MemorySegment queryAttributes(final Field... fields) throws ControlException {
        // Allocate attributes struct and set requested fields
        var listener_attr = ucp_listener_attr_t.allocate(MemorySession.openImplicit());
        ucp_listener_attr_t.field_mask$set(listener_attr, BitMask.longOf(fields));

        // Query requested fields
        var status = ucp_listener_query(address(), listener_attr);
        if (Status.isNot(status, Status.OK)) {
            throw new ControlException(status);
        }

        return listener_attr;
    }

    public enum Field implements LongFlag {
        SOCKADDR(UCP_LISTENER_ATTR_FIELD_SOCKADDR());

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
