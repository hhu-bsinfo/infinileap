package de.hhu.bsinfo.infinileap.rdma;

import de.hhu.bsinfo.infinileap.nio.Watchable;
import de.hhu.bsinfo.infinileap.util.FileDescriptor;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.linux.rdma.infinileap_h.rdma_event_channel;

public class EventChannel extends NativeObject implements Watchable {

    private final FileDescriptor fileDescriptor;

    protected EventChannel(MemoryAddress address) {
        super(address, rdma_event_channel.$LAYOUT());
        this.fileDescriptor = FileDescriptor.from(rdma_event_channel.fd$get(segment()));
    }

    protected EventChannel(MemorySegment segment) {
        super(segment);
        this.fileDescriptor = FileDescriptor.from(rdma_event_channel.fd$get(segment()));
    }

    @Override
    public FileDescriptor descriptor() {
        return fileDescriptor;
    }
}
