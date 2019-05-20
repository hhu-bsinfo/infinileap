package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;

public class ReceiveWorkRequest extends Struct {

    private static final StructInformation INFO = StructUtil.getInfo("ibv_recv_wr");
    public static final int SIZE = INFO.structSize.get();

    private final NativeLong id = new NativeLong(getByteBuffer(), INFO.getOffset("wr_id"));
    private final NativeLong next = new NativeLong(getByteBuffer(), INFO.getOffset("next"));
    private final NativeLong listHandle = new NativeLong(getByteBuffer(), INFO.getOffset("sg_list"));
    private final NativeInteger listLength = new NativeInteger(getByteBuffer(), INFO.getOffset("num_sge"));

    public ReceiveWorkRequest() {
        super(SIZE);
    }

    public ReceiveWorkRequest(final long handle) {
        super(handle, SIZE);
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
