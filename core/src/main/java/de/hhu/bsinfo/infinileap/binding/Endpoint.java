package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import static org.openucx.ucx_h.ucp_tag_send_nbx;

public class Endpoint extends NativeObject {

    /* package-private */ Endpoint(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
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
}
