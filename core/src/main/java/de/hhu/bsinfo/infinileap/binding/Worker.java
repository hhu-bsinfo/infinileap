package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import de.hhu.bsinfo.infinileap.common.multiplex.Watchable;
import de.hhu.bsinfo.infinileap.common.io.FileDescriptor;
import java.lang.foreign.*;

import static org.openucx.Communication.ucp_request_cancel;
import static org.openucx.Communication.ucp_request_free;
import static org.openucx.OpenUcx.*;
import static org.openucx.Communication.ucp_tag_recv_nbx;

public class Worker extends NativeObject implements Watchable, AutoCloseable {

    private static final int NO_PROGRESS = 0;
    private static final Tag TAG_MASK_FULL = Tag.of(0xffffffffffffffffL);

    private final Context context;

    private final WorkerParameters workerParameters;

    /* package-private */ Worker(MemoryAddress address, Context context, WorkerParameters workerParameters) {
        super(address, ValueLayout.ADDRESS);
        this.context = context;
        this.workerParameters = workerParameters;
    }

    public WorkerAddress getAddress() throws ControlException {
        try (var session = MemorySession.openConfined()) {
            var pointer = MemorySegment.allocateNative(ValueLayout.ADDRESS, session);
            var length = MemorySegment.allocateNative(ValueLayout.JAVA_LONG, session);
            var status = ucp_worker_get_address(
                    Parameter.of(this),
                    pointer,
                    length
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new WorkerAddress(pointer.get(ValueLayout.ADDRESS, 0L), length.get(ValueLayout.JAVA_LONG, 0L));
        }
    }

    public WorkerProgress progress() {
        return ucp_worker_progress(address()) == NO_PROGRESS ? WorkerProgress.IDLE : WorkerProgress.ACTIVE;
    }

    public Status arm() {
        return Status.of(ucp_worker_arm(Parameter.of(this)));
    }

    public Status await() {
        return Status.of(ucp_worker_wait(Parameter.of(this)));
    }

    public Status signal() {
        return Status.of(ucp_worker_signal(Parameter.of(this)));
    }

    public Endpoint createEndpoint(EndpointParameters parameters) throws ControlException {
        try (var session = MemorySession.openConfined()) {
            var pointer = MemorySegment.allocateNative(ValueLayout.ADDRESS, session);
            var status = ucp_ep_create(
                    Parameter.of(this),
                    Parameter.of(parameters),
                    pointer
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new Endpoint(pointer.get(ValueLayout.ADDRESS, 0L), this, parameters);
        }
    }

    public Listener createListener(ListenerParameters parameters) throws ControlException {
        try (var session = MemorySession.openConfined()) {
            var pointer = MemorySegment.allocateNative(ValueLayout.ADDRESS, session);
            var status = ucp_listener_create(
                    Parameter.of(this),
                    Parameter.of(parameters),
                    pointer
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new Listener(pointer.get(ValueLayout.ADDRESS, 0L), parameters);
        }
    }

    public long receiveTagged(NativeObject object, Tag tag) {
        return receiveTagged(object.address(), object.byteSize(), tag, TAG_MASK_FULL, RequestParameters.EMPTY);
    }

    public long receiveTagged(NativeObject object, Tag tag, RequestParameters parameters) {
        return receiveTagged(object.address(), object.byteSize(), tag, TAG_MASK_FULL, parameters);
    }

    public long receiveTagged(NativeObject object, Tag tag, Tag tagMask) {
        return receiveTagged(object.address(), object.byteSize(), tag, tagMask, RequestParameters.EMPTY);
    }

    public long receiveTagged(NativeObject object, Tag tag, Tag tagMask, RequestParameters parameters) {
        return receiveTagged(object.address(), object.byteSize(), tag, tagMask, parameters);
    }

    public long receiveTagged(MemorySegment buffer, Tag tag) {
        return receiveTagged(buffer, tag, TAG_MASK_FULL, RequestParameters.EMPTY);
    }

    public long receiveTagged(MemorySegment buffer, Tag tag, RequestParameters parameters) {
        return receiveTagged(buffer, tag, TAG_MASK_FULL, parameters);
    }

    public long receiveTagged(MemorySegment buffer, Tag tag, Tag tagMask) {
        return receiveTagged(buffer, tag, tagMask, RequestParameters.EMPTY);
    }

    public long receiveTagged(MemorySegment buffer, Tag tag, Tag tagMask, RequestParameters parameters) {
        return receiveTagged(buffer.address(), buffer.byteSize(), tag, tagMask, parameters);
    }

    private long receiveTagged(MemoryAddress address, long byteSize, Tag tag, Tag tagMask, RequestParameters parameters) {
        return ucp_tag_recv_nbx(
                Parameter.of(this),
                address,
                byteSize,
                tag.getValue(),
                tagMask.getValue(),
                Parameter.of(parameters)
        );
    }

    public void setHandler(HandlerParameters parameters) throws ControlException {
        var status = ucp_worker_set_am_recv_handler(
                Parameter.of(this),
                Parameter.of(parameters)
        );

        if (Status.isNot(status, Status.OK)) {
            throw new ControlException(status);
        }
    }

    public FileDescriptor fileDescriptor() throws ControlException {
        try (var session = MemorySession.openConfined()) {
            var descriptor = MemorySegment.allocateNative(ValueLayout.JAVA_INT, session);
            var status = ucp_worker_get_efd(
                Parameter.of(this),
                descriptor
            );


            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return FileDescriptor.of(descriptor.get(ValueLayout.JAVA_INT, 0L));
        }
    }

    public void cancelRequest(long request) {
        if (!Status.isStatus(request)) {
            ucp_request_cancel(Parameter.of(this), request);
            ucp_request_free(request);
        }
    }

    public Context context() {
        return context;
    }

    @Override
    public void close() {
        ucp_worker_destroy(segment());
    }

    @Override
    public FileDescriptor descriptor() {
        try {
            return fileDescriptor();
        } catch (ControlException e) {
            return null;
        }
    }

    @Override
    public String name() {
        return Worker.class.getSimpleName();
    }
}
