package de.hhu.bsinfo.infinileap.multiplex;

import de.hhu.bsinfo.infinileap.util.BitMask;

import java.lang.invoke.ConstantBootstraps;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class SelectionKey<T> {

    private static final VarHandle INTERESTS =
            ConstantBootstraps.fieldVarHandle(
                    MethodHandles.lookup(),
                    "interestOps",
                    VarHandle.class,
                    SelectionKey.class, int.class);

    /**
     * The {@link Watchable} used to register this selection key.
     */
    private final Watchable watchable;

    /**
     * A user-provided object attached to this {@link SelectionKey}.
     */
    private final T attachment;

    /**
     * The set of operations this key is interested in.
     */
    private volatile int interestOps;

    /**
     * The set of operations that are ready for this key.
     */
    private volatile int readyOps;

    /**
     * The selector associated with this key.
     */
    private final EpollSelector<T> selector;

    public SelectionKey(Watchable watchable, EpollSelector<T> selector) {
        this(watchable, null, selector);
    }

    public SelectionKey(Watchable watchable, T attachment, EpollSelector<T> selector) {
        this.watchable = watchable;
        this.attachment = attachment;
        this.selector = selector;
    }

    public boolean hasInterest(EventType type) {
        return BitMask.isSet(interestOps, type);
    }

    public boolean isReady(EventType type) {
        return BitMask.isSet(readyOps, type);
    }

    public void interestOps(EventType... types) {
        interestOps(BitMask.intOf(types));
    }

    void interestOps(int ops) {
        INTERESTS.getAndSet(ops);
    }

    public void readyOps(EventType... types) {
        readyOps(BitMask.intOf(types));
    }

    void readyOps(int ops) {
        readyOps = ops;
    }

    public T attachment() {
        return attachment;
    }

    public EpollSelector<T> selector() {
        return selector;
    }

}
