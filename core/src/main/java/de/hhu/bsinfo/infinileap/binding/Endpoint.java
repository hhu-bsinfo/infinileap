package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import de.hhu.bsinfo.infinileap.util.Parameter;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import static org.openucx.ucx_h.*;

public class Endpoint extends NativeObject {

    /* package-private */ Endpoint(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public Request sendTagged(MemorySegment message, Tag tag) {
        return sendTagged(message, tag, RequestParameters.EMPTY);
    }

    public Request sendTagged(MemorySegment message, Tag tag, RequestParameters parameters) {
        var address = ucp_tag_send_nbx(
                this.address(),
                message,
                message.byteSize(),
                tag.getValue(),
                parameters.address()
        );

        return Request.of(address);
    }

    public Request sendStream(MemorySegment message) {
        return sendStream(message, RequestParameters.EMPTY);
    }

    public Request sendStream(MemorySegment message, RequestParameters parameters) {
        var address = ucp_stream_send_nbx(
                Parameter.of(this),
                message,
                message.byteSize(),
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

    public Request get(MemorySegment source, MemoryAddress remoteAddress, RemoteKey key) {
        return get(source, remoteAddress, key, RequestParameters.EMPTY);
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

    public Request flush(RequestParameters parameters) {
        var address = ucp_ep_flush_nbx(
                Parameter.of(this),
                Parameter.of(parameters)
        );

        return Request.of(address);
    }
}
