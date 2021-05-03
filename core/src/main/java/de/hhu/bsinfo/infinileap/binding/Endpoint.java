package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.primitive.NativeInteger;
import de.hhu.bsinfo.infinileap.primitive.NativeLong;
import jdk.incubator.foreign.*;
import org.openucx.Communication;

import static org.openucx.Communication.*;
import static org.openucx.OpenUcx.ucp_ep_rkey_unpack;


public class Endpoint extends NativeObject {

    private static final long SINGLE_ELEMENT = 1L;

    /* package-private */ Endpoint(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
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
                header,
                header.byteSize(),
                data.address(),
                data.byteSize(),
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

    public long put(MemorySegment source, MemoryAddress remoteAddress, RemoteKey key) {
        return put(source, remoteAddress, key, RequestParameters.EMPTY);
    }

    public long put(MemorySegment source, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        return ucp_put_nbx(
                Parameter.of(this),
                source,
                source.byteSize(),
                remoteAddress.toRawLongValue(),
                key.address(),
                Parameter.of(parameters)
        );
    }

    public long get(MemorySegment target, MemoryAddress remoteAddress, RemoteKey key) {
        return get(target, remoteAddress, key, RequestParameters.EMPTY);
    }

    public long get(MemorySegment target, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        return ucp_get_nbx(
                Parameter.of(this),
                target,
                target.byteSize(),
                remoteAddress.toRawLongValue(),
                key.address(),
                Parameter.of(parameters)
        );
    }

    public long atomic(AtomicOperation operation, NativeLong value, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        return atomic(operation, value.segment(), 1, remoteAddress, key, parameters);
    }

    public long atomic(AtomicOperation operation, NativeInteger value, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        return atomic(operation, value.segment(), 1, remoteAddress, key, parameters);
    }

    public long atomic(AtomicOperation operation, MemorySegment buffer, int count, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        return ucp_atomic_op_nbx(
                Parameter.of(this),
                operation.getValue(),
                buffer,
                count,
                remoteAddress.toRawLongValue(),
                key.address(),
                Parameter.of(parameters)
        );
    }

    public RemoteKey unpack(MemoryDescriptor descriptor) throws ControlException {
        var keySegment = descriptor.keySegment();
        try (var scope = ResourceScope.newConfinedScope()) {
            var pointer = MemorySegment.allocateNative(CLinker.C_POINTER, scope);
            var status = ucp_ep_rkey_unpack(
                Parameter.of(this),
                keySegment,
                pointer.address()
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new RemoteKey(MemoryAccess.getAddress(pointer));
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
}
