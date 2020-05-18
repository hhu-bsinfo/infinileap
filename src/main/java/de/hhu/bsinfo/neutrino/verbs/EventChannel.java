package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.LinkNative;
import org.agrona.concurrent.AtomicBuffer;

@LinkNative("rdma_event_channel")
public final class EventChannel extends Struct {

    private final NativeInteger fileDescriptor = integerField("fd");

    EventChannel() {}

    EventChannel(long handle) {
        super(handle);
    }

    EventChannel(AtomicBuffer buffer, int offset) {
        super(buffer, offset);
    }

    public int getFileDescriptor() {
        return fileDescriptor.get();
    }

    public void setFileDescriptor(final int value) {
        fileDescriptor.set(value);
    }
}
