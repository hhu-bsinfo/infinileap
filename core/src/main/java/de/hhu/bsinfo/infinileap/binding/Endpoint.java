package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import de.hhu.bsinfo.infinileap.common.network.NativeInetSocketAddress;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;
import de.hhu.bsinfo.infinileap.primitive.NativeInteger;
import de.hhu.bsinfo.infinileap.primitive.NativeLong;
import de.hhu.bsinfo.infinileap.util.Requests;
import lombok.extern.slf4j.Slf4j;
import org.openucx.ucp_ep_attr_t;
import org.openucx.ucp_listener_attr_t;

import java.lang.foreign.*;
import java.net.InetSocketAddress;

import static org.openucx.Communication.*;
import static org.openucx.OpenUcx.ucp_ep_rkey_unpack;
import static org.openucx.OpenUcx.ucp_ep_query;
import static org.openucx.OpenUcx.UCP_EP_ATTR_FIELD_LOCAL_SOCKADDR;
import static org.openucx.OpenUcx.UCP_EP_ATTR_FIELD_REMOTE_SOCKADDR;

@Slf4j
public class Endpoint extends NativeObject implements AutoCloseable {

    private static final long SINGLE_ELEMENT = 1L;

    private final Worker worker;

    private final EndpointParameters endpointParameters;

    private static final RequestParameters FORCE_CLOSE = new RequestParameters()
            .setFlags(RequestParameters.Flag.CLOSE_FORCE);

    /* package-private */ Endpoint(MemorySegment base, Worker worker, EndpointParameters endpointParameters) {
        super(base, ValueLayout.ADDRESS);
        this.worker = worker;
        this.endpointParameters = endpointParameters;
    }

    public long sendTagged(NativeObject object, Tag tag) {
        return sendTagged(object.segment(), tag, RequestParameters.EMPTY);
    }

    public long sendTagged(NativeObject object, Tag tag, RequestParameters parameters) {
        return sendTagged(object.segment(), tag, parameters);
    }

    public long sendTagged(MemorySegment message, Tag tag) {
        return sendTagged(message, tag, RequestParameters.EMPTY);
    }

    public long sendTagged(MemorySegment message, Tag tag, RequestParameters parameters) {
        return ucp_tag_send_nbx(
                Parameter.of(this),
                message,
                message.byteSize(),
                tag.getValue(),
                Parameter.of(parameters)
        );
    }

    public long sendStream(NativeObject object, RequestParameters parameters) {
        return sendStream(object.segment(), SINGLE_ELEMENT, parameters);
    }

    public long sendStream(MemorySegment buffer, long count, RequestParameters parameters) {
        return ucp_stream_send_nbx(
                Parameter.of(this),
                buffer,
                count,
                Parameter.of(parameters)
        );
    }

    public long sendActive(Identifier identifier, MemorySegment header, MemorySegment data, RequestParameters parameters) {
        return ucp_am_send_nbx(
                Parameter.of(this),
                identifier.value(),
                header == null ? MemorySegment.NULL : header,
                header == null ? 0L : header.byteSize(),
                data == null ? MemorySegment.NULL : data,
                data == null ? 0L : data.byteSize(),
                Parameter.of(parameters)
        );
    }

    public long receiveStream(MemorySegment buffer, long count, NativeLong length) {
        return receiveStream(buffer, count, length, RequestParameters.EMPTY);
    }

    public long receiveStream(MemorySegment buffer, long count, NativeLong length, RequestParameters parameters) {
        return ucp_stream_recv_nbx(
                Parameter.of(this),
                buffer,
                count,
                Parameter.of(length),
                Parameter.of(parameters)
        );
    }

    public long put(MemorySegment source, MemorySegment remoteAddress, RemoteKey key) {
        return put(source, remoteAddress, key, RequestParameters.EMPTY);
    }

    public long put(MemorySegment source, MemorySegment remoteAddress, RemoteKey key, RequestParameters parameters) {
        return ucp_put_nbx(
                Parameter.of(this),
                source,
                source.byteSize(),
                remoteAddress.address(),
                key.segment(),
                Parameter.of(parameters)
        );
    }

    public long get(MemorySegment target, MemorySegment remoteAddress, RemoteKey key) {
        return get(target, remoteAddress, key, RequestParameters.EMPTY);
    }

    public long get(MemorySegment target, MemorySegment remoteAddress, RemoteKey key, RequestParameters parameters) {
        return ucp_get_nbx(
                Parameter.of(this),
                target,
                target.byteSize(),
                remoteAddress.address(),
                key.segment(),
                Parameter.of(parameters)
        );
    }

    public long atomic(AtomicOperation operation, NativeLong value, MemorySegment remoteAddress, RemoteKey key, RequestParameters parameters) {
        return atomic(operation, value.segment(), 1, remoteAddress, key, parameters);
    }

    public long atomic(AtomicOperation operation, NativeInteger value, MemorySegment remoteAddress, RemoteKey key, RequestParameters parameters) {
        return atomic(operation, value.segment(), 1, remoteAddress, key, parameters);
    }

    public long atomic(AtomicOperation operation, MemorySegment buffer, int count, MemorySegment remoteAddress, RemoteKey key, RequestParameters parameters) {
        return ucp_atomic_op_nbx(
                Parameter.of(this),
                operation.getValue(),
                buffer,
                count,
                remoteAddress.address(),
                key.segment(),
                Parameter.of(parameters)
        );
    }

    public RemoteKey unpack(MemoryDescriptor descriptor) throws ControlException {
        var keySegment = descriptor.keySegment();
        try (var arena = Arena.openConfined()) {
            var pointer = arena.allocate(ValueLayout.ADDRESS);
            var status = ucp_ep_rkey_unpack(
                Parameter.of(this),
                keySegment,
                pointer
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new RemoteKey(pointer.get(ValueLayout.ADDRESS, 0L));
        }
    }

    public long flush() {
        return flush(RequestParameters.EMPTY);
    }

    public long flush(RequestParameters parameters) {
        return ucp_ep_flush_nbx(
                Parameter.of(this),
                Parameter.of(parameters)
        );
    }

    public long closeNonBlocking() {
        return closeNonBlocking(RequestParameters.EMPTY);
    }

    public long closeNonBlocking(RequestParameters parameters) {
        return ucp_ep_close_nbx(
                Parameter.of(this),
                Parameter.of(parameters)
        );
    }

    public InetSocketAddress getLocalAddress() throws ControlException {
        // Extract client address and address family from attributes
        var attributes = queryAttributes(Field.LOCAL_SOCKADDR);
        var localAddress = ucp_ep_attr_t.local_sockaddr$slice(attributes);

        // Convert native address struct to InetSocketAddress
        var nativeAddress = NativeInetSocketAddress.wrap(localAddress);
        return nativeAddress.toInetSocketAddress();
    }

    public InetSocketAddress getRemoteAddress() throws ControlException {
        // Extract client address and address family from attributes
        var attributes = queryAttributes(Field.REMOTE_SOCKADDR);
        var localAddress = ucp_ep_attr_t.remote_sockaddr$slice(attributes);

        // Convert native address struct to InetSocketAddress
        var nativeAddress = NativeInetSocketAddress.wrap(localAddress);
        return nativeAddress.toInetSocketAddress();
    }

    public Worker worker() {
        return worker;
    }

    public Context context() {
        return worker.context();
    }

    @Override
    public void close() {
        var request = ucp_ep_close_nbx(
                Parameter.of(this),
                Parameter.of(RequestParameters.EMPTY)
        );

        // Endpoint was closed immediately
        if (Status.is(request, Status.OK)) {
            log.debug("Closed endpoint immediately");
            return;
        }

        // Error closing endpoint
        if (Status.isError(request)) {
            throw new RuntimeException(
                String.format(
                        "Closing endpoint failed with error %s",
                        Status.of((int) request)
                )
            );
        }

        try {
            Requests.await(worker, request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.debug("Closed endpoint");
    }

    private MemorySegment queryAttributes(final Field... fields) throws ControlException {
        // Allocate attributes struct and set requested fields
        var endpoint_attr = ucp_ep_attr_t.allocate(SegmentAllocator.nativeAllocator(SegmentScope.auto()));
        ucp_ep_attr_t.field_mask$set(endpoint_attr, BitMask.longOf(fields));

        // Query requested fields
        var status = ucp_ep_query(segment(), endpoint_attr);
        if (Status.isNot(status, Status.OK)) {
            throw new ControlException(status);
        }

        return endpoint_attr;
    }

    private enum Field implements LongFlag {
        LOCAL_SOCKADDR(UCP_EP_ATTR_FIELD_LOCAL_SOCKADDR()),
        REMOTE_SOCKADDR(UCP_EP_ATTR_FIELD_REMOTE_SOCKADDR());

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
