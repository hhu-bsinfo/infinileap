package de.hhu.bsinfo.neutrino.verbs.panama.util;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;

import java.util.function.Supplier;

public class Struct implements NativeObject {

    /**
     * This struct's backing memory segment.
     */
    private final MemorySegment segment;

    /**
     * This struct's base address within its segment.
     */
    private final MemoryAddress baseAddress;

    protected Struct(Supplier<MemorySegment> segmentSupplier) {
        segment = segmentSupplier.get();
        baseAddress = segment.baseAddress();
    }

    protected Struct(MemoryLayout layout, MemoryAddress address) {
        if (address.equals(MemoryAddress.NULL)) {
            throw new IllegalArgumentException("memory address is pointing at null");
        }

        segment = MemorySegment.ofNativeRestricted(address, layout.byteSize(), Thread.currentThread(), null, null);
        baseAddress = segment.baseAddress();
    }

    @Override
    public MemoryAddress memoryAddress() {
        return baseAddress;
    }

    @Override
    public long sizeOf() {
        return segment.byteSize();
    }

    @Override
    public void close() {
        segment.close();
    }
}
