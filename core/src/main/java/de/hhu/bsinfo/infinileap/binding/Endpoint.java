package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.primitive.NativeInteger;
import de.hhu.bsinfo.infinileap.primitive.NativeLong;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import static org.openucx.ucx_h.*;

public class Endpoint extends NativeObject {

    private static final long SINGLE_ELEMENT = 1L;

    /* package-private */ Endpoint(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public Request sendTagged(NativeObject object, Tag tag) {
        return sendTagged(object.segment(), tag, RequestParameters.EMPTY);
    }

    public Request sendTagged(NativeObject object, Tag tag, RequestParameters parameters) {
        return sendTagged(object.segment(), tag, parameters);
    }

    public Request sendTagged(MemorySegment message, Tag tag) {
        return sendTagged(message, tag, RequestParameters.EMPTY);
    }

    public Request sendTagged(MemorySegment message, Tag tag, RequestParameters parameters) {
        var address = ucp_tag_send_nbx(
                Parameter.of(this),
                message,
                message.byteSize(),
                tag.getValue(),
                Parameter.of(parameters)
        );

        return Request.of(address);
    }

    public Request sendStream(NativeObject object, RequestParameters parameters) {
        return sendStream(object.segment(), SINGLE_ELEMENT, parameters);
    }

    public Request sendStream(MemorySegment buffer, long count, RequestParameters parameters) {
        var address = ucp_stream_send_nbx(
                Parameter.of(this),
                buffer,
                count,
                Parameter.of(parameters)
        );

        return Request.of(address);
    }

    public Request receiveStream(MemorySegment buffer, long count, NativeLong length) {
        return receiveStream(buffer, count, length, RequestParameters.EMPTY);
    }

    public Request receiveStream(MemorySegment buffer, long count, NativeLong length, RequestParameters parameters) {
        var address = ucp_stream_recv_nbx(
                Parameter.of(this),
                buffer,
                count,
                Parameter.of(length),
                Parameter.of(parameters)
        );

        return Request.of(address);
    }

    public Request put(MemorySegment source, MemoryAddress remoteAddress, RemoteKey key) {
        return put(source, remoteAddress, key, RequestParameters.EMPTY);
    }

    public Request put(MemorySegment source, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        var address = ucp_put_nbx(
                Parameter.of(this),
                source,
                source.byteSize(),
                remoteAddress.toRawLongValue(),
                key.address(),
                Parameter.of(parameters)
        );

        return Request.of(address);
    }

    public Request get(MemorySegment target, MemoryAddress remoteAddress, RemoteKey key) {
        return get(target, remoteAddress, key, RequestParameters.EMPTY);
    }

    public Request get(MemorySegment target, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        var address = ucp_get_nbx(
                Parameter.of(this),
                target,
                target.byteSize(),
                remoteAddress.toRawLongValue(),
                key.address(),
                Parameter.of(parameters)
        );

        return Request.of(address);
    }

    public Request atomic(AtomicOperation operation, NativeLong value, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        return atomic(operation, value.segment(), 1, remoteAddress, key, parameters);
    }

    public Request atomic(AtomicOperation operation, NativeInteger value, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        return atomic(operation, value.segment(), 1, remoteAddress, key, parameters);
    }

    public Request atomic(AtomicOperation operation, MemorySegment buffer, int count, MemoryAddress remoteAddress, RemoteKey key, RequestParameters parameters) {
        var address = ucp_atomic_op_nbx(
                Parameter.of(this),
                operation.getValue(),
                buffer,
                count,
                remoteAddress.toRawLongValue(),
                key.address(),
                Parameter.of(parameters)
        );

        return Request.of(address);
    }

    public RemoteKey unpack(MemoryDescriptor descriptor) {
        var keySegment = descriptor.keySegment();
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {

            var status = ucp_ep_rkey_unpack(
                Parameter.of(this),
                keySegment,
                pointer.address()
            );

            if (Status.OK.isNot(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            return new RemoteKey(MemoryAccess.getAddress(pointer));
        }
    }

    public Request flush(RequestParameters parameters) {
        var address = ucp_ep_flush_nbx(
                Parameter.of(this),
                Parameter.of(parameters)
        );

        return Request.of(address);
    }
}
