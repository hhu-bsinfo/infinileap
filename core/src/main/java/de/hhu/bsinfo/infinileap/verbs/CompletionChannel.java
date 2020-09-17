package de.hhu.bsinfo.infinileap.verbs;

import static org.linux.rdma.infinileap_h.*;

import de.hhu.bsinfo.infinileap.nio.Watchable;
import de.hhu.bsinfo.infinileap.util.FileDescriptor;
import de.hhu.bsinfo.infinileap.util.NativeObject;
import jdk.incubator.foreign.MemoryAddress;

public final class CompletionChannel extends NativeObject implements Watchable {

    private final FileDescriptor descriptor;

    public CompletionChannel() {
        super(ibv_comp_channel.allocate());
        this.descriptor = FileDescriptor.from(ibv_comp_channel.fd$get(segment()));
    }

    public CompletionChannel(MemoryAddress address) {
        super(address, ibv_comp_channel.$LAYOUT());
        this.descriptor = FileDescriptor.from(ibv_comp_channel.fd$get(segment()));
    }

    public MemoryAddress getContext() {
        return ibv_comp_channel.context$get(segment());
    }

    public int getFileDescriptor() {
        return ibv_comp_channel.fd$get(segment());
    }

    public int getReferenceCount() {
        return ibv_comp_channel.refcnt$get(segment());
    }

    public void setContext(final MemoryAddress value) {
        ibv_comp_channel.context$set(segment(), value);
    }

    public void setFileDescriptor(final int value) {
        ibv_comp_channel.fd$set(segment(), value);
    }

    public void setReferenceCount(final int value) {
        ibv_comp_channel.refcnt$set(segment(), value);
    }

    @Override
    public FileDescriptor descriptor() {
        return descriptor;
    }
}