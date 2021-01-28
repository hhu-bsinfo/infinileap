package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.FileDescriptor;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import static org.openucx.ucx_h.*;

public class Worker extends NativeObject {

    /* package-private */ Worker(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public WorkerAddress getAddress() {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER);
             var length = MemorySegment.allocateNative(CLinker.C_LONG)) {



            var status = ucp_worker_get_address(
                    Parameter.of(this),
                    pointer,
                    length
            );

            if (Status.OK.isNot(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            return new WorkerAddress(MemoryAccess.getAddress(pointer), MemoryAccess.getLong(length));
        }
    }

    public WorkerProgress progress() {
        return ucp_worker_progress(this.address()) == 0 ? WorkerProgress.IDLE : WorkerProgress.ACTIVE;
    }

    public Status arm() {
        return Status.of(ucp_worker_arm(Parameter.of(this)));
    }

    public Status await() {
        return Status.of(ucp_worker_wait(Parameter.of(this)));
    }

    public Status wake() {
        return Status.of(ucp_worker_signal(Parameter.of(this)));
    }

    public Endpoint createEndpoint(EndpointParameters parameters) {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {
            var status = ucp_ep_create(
                    Parameter.of(this),
                    Parameter.of(parameters),
                    pointer
            );

            if (Status.OK.isNot(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            return new Endpoint(MemoryAccess.getAddress(pointer));
        }
    }

    public Listener createListener(ListenerParameters parameters) {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {
            var status = ucp_listener_create(
                    Parameter.of(this),
                    Parameter.of(parameters),
                    pointer
            );

            if (Status.OK.isNot(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            return new Listener(MemoryAccess.getAddress(pointer));
        }
    }

    public Request receiveTagged(NativeObject object, Tag tag) {
        return receiveTagged(object.segment(), tag, RequestParameters.EMPTY);
    }

    public Request receiveTagged(NativeObject object, Tag tag, RequestParameters parameters) {
        return receiveTagged(object.segment(), tag, parameters);
    }

    public Request receiveTagged(MemorySegment buffer, Tag tag) {
        return receiveTagged(buffer, tag, RequestParameters.EMPTY);
    }

    public Request receiveTagged(MemorySegment buffer, Tag tag, RequestParameters parameters) {
        var address = ucp_tag_recv_nbx(
                Parameter.of(this),
                buffer,
                buffer.byteSize(),
                tag.getValue(),
                tag.getValue(),
                Parameter.of(parameters)
        );

        return Request.of(address);
    }

    public FileDescriptor fileDescriptor() {
        try (var descriptor = MemorySegment.allocateNative(CLinker.C_INT)) {
            var status = ucp_worker_get_efd(
                Parameter.of(this),
                descriptor
            );


            if (Status.OK.isNot(status)) {
                // TODO(krakowski):
                //  Error handling using Exception or other appropriate mechanism
                return null;
            }

            return FileDescriptor.of(MemoryAccess.getInt(descriptor));
        }
    }

    @Override
    public void close() {
        ucp_worker_destroy(segment());
        super.close();
    }
}
