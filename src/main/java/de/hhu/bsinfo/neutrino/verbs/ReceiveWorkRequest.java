package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import de.hhu.bsinfo.neutrino.util.Linkable;

import java.util.concurrent.atomic.AtomicLong;

@LinkNative("ibv_recv_wr")
public class ReceiveWorkRequest extends Struct implements Linkable<ReceiveWorkRequest>, Poolable {

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

    public void setId(final long value) {
        id.set(value);
    }

    public void setScatterGatherElement(final ScatterGatherElement singleSge) {
        listHandle.set(singleSge.getHandle());
        listLength.set(1);
    }

    public void setScatterGatherElement(final ScatterGatherElement.Array list) {
        listHandle.set(list.getHandle());
        listLength.set((int) list.getNativeSize());
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

        private long id;
        private long listHandle;
        private int listLength;

        public Builder withId(final int id) {
            this.id = id;
            return this;
        }

        public Builder withScatterGatherElement(final ScatterGatherElement singleSge) {
            listHandle = singleSge.getHandle();
            listLength = 1;
            return this;
        }

        public Builder withScatterGatherList(final ScatterGatherElement.Array list) {
            listHandle = list.getHandle();
            listLength = (int) list.getNativeSize();
            return this;
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
