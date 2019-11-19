package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Linkable;

import java.util.concurrent.atomic.AtomicLong;

@LinkNative("ibv_recv_wr")
public class ReceiveWorkRequest extends Struct implements Linkable<ReceiveWorkRequest> {

    private final NativeLong id = longField("wr_id");
    private final NativeLong next = longField("next");
    private final NativeLong listHandle = longField("sg_list");
    private final NativeInteger listLength = integerField("num_sge");

    ReceiveWorkRequest() {}

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

    @Override
    public void linkWith(ReceiveWorkRequest other) {
        next.set(other.getHandle());
    }

    @Override
    public void unlink() {
        next.set(0);
    }

    @Override
    public String toString() {
        return "ReceiveWorkRequest {" +
                "\n\tid=" + id +
                ",\n\tnext=" + next +
                ",\n\tlistHandle=" + listHandle +
                ",\n\tlistLength=" + listLength +
                "\n}";
    }

    public static final class Builder {

        private static final AtomicLong ID_COUNTER = new AtomicLong(0);

        private final long id;
        private long listHandle;
        private int listLength;

        public Builder() {
            id = ID_COUNTER.getAndIncrement();
        }

        public Builder(final ScatterGatherElement singleSge) {
            id = ID_COUNTER.getAndIncrement();
            listHandle = singleSge.getHandle();
            listLength = 1;
        }

        public Builder(final ScatterGatherElement.Array list) {
            id = ID_COUNTER.getAndIncrement();
            listHandle = list.getHandle();
            listLength = (int) list.getNativeSize();
        }

        public ReceiveWorkRequest build() {
            var ret = new ReceiveWorkRequest();

            ret.setId(id);
            ret.setListHandle(listHandle);
            ret.setListLength(listLength);

            return ret;
        }
    }
}
