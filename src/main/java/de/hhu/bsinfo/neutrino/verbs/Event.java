package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;

@LinkNative("rdma_cm_event")
public final class Event extends Struct {

    private final NativeLong identifier = longField("id");
    private final NativeLong listenIdentifier = longField("listen_id");
    private final NativeInteger event = integerField("event");
    private final NativeInteger status = integerField("status");
    private final NativeLong conn = longField("param.conn");
    private final NativeLong ud = longField("param.ud");

    Event() {}

    Event(long handle) {
        super(handle);
    }

    Event(LocalBuffer buffer, long offset) {
        super(buffer, offset);
    }

    public long getIdentifier() {
        return identifier.get();
    }

    public long getListenIdentifier() {
        return listenIdentifier.get();
    }

    public int getEvent() {
        return event.get();
    }

    public int getStatus() {
        return status.get();
    }

    public long getConn() {
        return conn.get();
    }

    public long getUd() {
        return ud.get();
    }

    public void setIdentifier(final long value) {
        identifier.set(value);
    }

    public void setListenIdentifier(final long value) {
        listenIdentifier.set(value);
    }

    public void setEvent(final int value) {
        event.set(value);
    }

    public void setStatus(final int value) {
        status.set(value);
    }

    public void setConn(final long value) {
        conn.set(value);
    }

    public void setUd(final long value) {
        ud.set(value);
    }
}
