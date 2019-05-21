package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;

public class ReceiveWorkRequest extends Struct {

    private final NativeLong id = longField("wr_id");
    private final NativeLong next = longField("next");
    private final NativeLong listHandle = longField("sg_list");
    private final NativeInteger listLength = integerField("num_sge");

    public ReceiveWorkRequest() {
        super("ibv_recv_wr");
    }

    public ReceiveWorkRequest(final long handle) {
        super("ibv_recv_wr", handle);
    }

    long getId() {
        return id.get();
    }

    long getNext() {
        return next.get();
    }

    long getListHandle() {
        return listHandle.get();
    }

    int getListLength() {
        return listLength.get();
    }

    void setId(final long value) {
        id.set(value);
    }

    void setNext(final long value) {
        next.set(value);
    }

    void setListHandle(final long value) {
        listHandle.set(value);
    }

    void setListLength(final int value) {
        listLength.set(value);
    }

}
