package de.hhu.bsinfo.infinileap.common.util;

import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import java.lang.foreign.*;

public class NativeObject {

    /**
     * This struct's backing memory segment.
     */
    private final MemorySegment segment;

    protected NativeObject(MemorySegment segment) {
        if (segment.address() == MemorySegment.NULL.address()) {
            throw new IllegalArgumentException("memory address is pointing at null");
        }

        if (!segment.scope().isAlive()) {
            throw new IllegalArgumentException("the provided segment's scope must be alive");
        }

        this.segment = segment;
    }

    protected NativeObject(MemorySegment base, MemoryLayout layout) {
        this(base, layout.byteSize());
    }

    protected NativeObject(MemorySegment base, long byteSize) {
        // Since accessing memory obtained from native functions is
        // considered dangerous, we need to create a restricted
        // MemorySegment first by using our base segment.
        this(MemoryUtil.wrap(base.address(), byteSize));
    }

    public long byteSize() {
        return segment.byteSize();
    }

    protected SegmentScope scope() {
        return segment.scope();
    }

    public final MemorySegment segment() {
        return segment;
    }

    public final byte[] toByteArray() {
        return segment.toArray(ValueLayout.JAVA_BYTE);
    }

    public final void hexDump() {
        MemoryUtil.dump(segment);
    }
}
