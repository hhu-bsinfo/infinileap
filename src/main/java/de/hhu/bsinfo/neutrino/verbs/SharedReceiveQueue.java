package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.struct.StructInformation;
import de.hhu.bsinfo.neutrino.util.StructUtil;

public class SharedReceiveQueue extends Struct {

    private static final StructInformation INFO = StructUtil.getInfo("ibv_srq");
    public static final int SIZE = INFO.structSize.get();

    private final NativeLong x = new NativeLong(getByteBuffer(), INFO.getOffset("abc"));

    public SharedReceiveQueue() {
        super(SIZE);
    }

    public SharedReceiveQueue(long handle) {
        super(handle, SIZE);
    }

    public long getX() {
        return x.get();
    }

    public void setX(long x) {
        this.x.set(x);
    }
}