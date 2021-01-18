package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;

import static org.openucx.ucx_h.ucp_rkey_buffer_release;

public class RemoteKey extends NativeObject {

    /* package-private */ RemoteKey(MemoryAddress address, long byteSize) {
        super(address, byteSize);
    }

    @Override
    public void close() {
        ucp_rkey_buffer_release(segment());
        super.close();
    }
}
