package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.binding.util.Status;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.*;
import org.openucx.ucx_h;

import static org.openucx.ucx_h.ucp_worker_get_address;

public class Worker extends NativeObject {

    /* package-private */ Worker(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public Address getAddress() {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER);
             var length = MemorySegment.allocateNative(CLinker.C_LONG)) {

            var status = ucp_worker_get_address(
                    this.address(),
                    pointer.address(),
                    length.address()
            );

            if (!Status.OK.is(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            return new Address(MemoryAccess.getAddress(pointer), MemoryAccess.getLong(length));
        }
    }

    public static final class Address extends NativeObject {

        Address(MemoryAddress address, long byteSize) {
            super(address, byteSize);
        }
    }
}
