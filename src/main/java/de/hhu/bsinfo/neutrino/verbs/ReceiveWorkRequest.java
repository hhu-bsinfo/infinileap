package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Poolable;

@LinkNative("ibv_recv_wr")
public class ReceiveWorkRequest extends Struct implements Poolable {

    private final NativeLong id = longField("wr_id");
    private final NativeLong next = longField("next");
    private final NativeLong listHandle = longField("sg_list");
    private final NativeInteger listLength = integerField("num_sge");

    public ReceiveWorkRequest() {
    }

    public ReceiveWorkRequest(final long handle) {
        super(handle);
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
