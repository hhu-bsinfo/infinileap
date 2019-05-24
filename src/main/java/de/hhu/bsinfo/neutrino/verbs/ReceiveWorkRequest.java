package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLinkedList.Linker;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Poolable;
import java.util.function.Consumer;

@LinkNative("ibv_recv_wr")
public class ReceiveWorkRequest extends Struct implements Poolable {

    private final NativeLong id = longField("wr_id");
    private final NativeLong next = longField("next");
    private final NativeLong listHandle = longField("sg_list");
    private final NativeInteger listLength = integerField("num_sge");

    public static final Linker<ReceiveWorkRequest> LINKER = (current, next) -> {
        current.next.set(next.getHandle());
    };

    public ReceiveWorkRequest() {
    }

    public ReceiveWorkRequest(Consumer<ReceiveWorkRequest> configurator) {
        configurator.accept(this);
    }

    public ReceiveWorkRequest(final long handle) {
        super(handle);
    }

    public long getId() {
        return id.get();
    }

    public long getNext() {
        return next.get();
    }

    public long getListHandle() {
        return listHandle.get();
    }

    public int getListLength() {
        return listLength.get();
    }

    public void setId(final long value) {
        id.set(value);
    }

    public void setNext(final long value) {
        next.set(value);
    }

    public void setListHandle(final long value) {
        listHandle.set(value);
    }

    public void setListLength(final int value) {
        listLength.set(value);
    }

}
