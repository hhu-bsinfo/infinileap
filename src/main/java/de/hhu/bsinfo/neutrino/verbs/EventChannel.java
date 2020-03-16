package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;

@LinkNative("rdma_event_channel")
public final class EventChannel extends Struct {

    private final NativeInteger fileDescriptor = integerField("fd");

    EventChannel() {}

    EventChannel(long handle) {
        super(handle);
    }

    EventChannel(LocalBuffer buffer, long offset) {
        super(buffer, offset);
    }

    public int getFileDescriptor() {
        return fileDescriptor.get();
    }

    public void setFileDescriptor(final int value) {
        fileDescriptor.set(value);
    }
}
