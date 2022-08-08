package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.network.NativeInetSocketAddress;
import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySession;
import org.openucx.*;

import java.net.InetSocketAddress;

import static org.openucx.OpenUcx.*;

public class ListenerParameters extends NativeObject {

    private NativeInetSocketAddress listenAddress;

    public ListenerParameters() {
        this(MemorySession.openImplicit());
    }

    public ListenerParameters(MemorySession session) {
        super(ucp_listener_params_t.allocate(session));
    }

    public ListenerParameters setListenAddress(InetSocketAddress address) {
        // Store reference to prevent garbage collection
        this.listenAddress = NativeInetSocketAddress.convert(address);

        var sockaddr = ucp_listener_params_t.sockaddr$slice(segment());
        ucs_sock_addr_t.addr$set(sockaddr, listenAddress.address());
        ucs_sock_addr_t.addrlen$set(sockaddr, listenAddress.getLength());

        addFieldMask(Field.SOCKET_ADDRESS);
        return this;
    }

    public ListenerParameters setConnectionHandler(ConnectionHandler handler) {
        return setConnectionHandler(handler, 0L);
    }

    public ListenerParameters setConnectionHandler(ConnectionHandler handler, long data) {
        var slice = ucp_listener_params_t.conn_handler$slice(segment());
        ucp_listener_conn_handler_t.cb$set(slice, handler.upcallAddress());
        ucp_listener_conn_handler_t.arg$set(slice, MemoryAddress.ofLong(data));
        addFieldMask(Field.CONNECTION_HANDLER);
        return this;
    }

    private long getFieldMask() {
        return ucp_listener_params_t.field_mask$get(segment());
    }

    private void setFieldMask(Field... fields) {
        ucp_listener_params_t.field_mask$set(segment(), BitMask.longOf(fields));
    }

    private void addFieldMask(Field... fields) {
        ucp_listener_params_t.field_mask$set(segment(), BitMask.longOf(fields) | getFieldMask());
    }

    public enum Field implements LongFlag {
        SOCKET_ADDRESS(UCP_LISTENER_PARAM_FIELD_SOCK_ADDR()),
        CONNECTION_HANDLER(UCP_LISTENER_PARAM_FIELD_CONN_HANDLER()),
        ACCEPT_HANDLER(UCP_LISTENER_PARAM_FIELD_ACCEPT_HANDLER());

        private final long value;

        Field(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }
}
