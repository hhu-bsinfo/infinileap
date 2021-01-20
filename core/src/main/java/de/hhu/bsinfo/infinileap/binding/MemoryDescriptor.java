package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.*;

import java.lang.invoke.VarHandle;

import static jdk.incubator.foreign.CLinker.*;

public class MemoryDescriptor extends NativeObject {

    private static final long KEY_DATA_SIZE = 64;

    private static final MemoryLayout LAYOUT = MemoryLayout.ofStruct(
            C_LONG.withName("buf_size"),
            C_POINTER.withName("buf_addr"),
            MemoryLayout.ofSequence(KEY_DATA_SIZE, C_CHAR).withName("key_data")
    );

    private static final VarHandle BUFFER_SIZE =
            LAYOUT.varHandle(long.class, MemoryLayout.PathElement.groupElement("buf_size"));

    private static final VarHandle BUFFER_ADDRESS = MemoryHandles.asAddressVarHandle(
            LAYOUT.varHandle(long.class, MemoryLayout.PathElement.groupElement("buf_addr")));

    private static final long KEY_DATA_OFFSET =
            LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("key_data"));

    public MemoryDescriptor() {
        super(MemorySegment.allocateNative(LAYOUT));
    }

    MemoryDescriptor(MemorySegment segment, MemorySegment remoteKey) {
        super(MemorySegment.allocateNative(LAYOUT));

        setBufferAddress(segment.address());
        setBufferSize(segment.byteSize());
        setRemoteKey(remoteKey);
    }

    private MemoryAddress getBufferAddress() {
        return (MemoryAddress) BUFFER_ADDRESS.get(segment());
    }

    private void setBufferAddress(MemoryAddress address) {
        BUFFER_ADDRESS.set(segment(), address);
    }

    private long getBufferSize() {
        return (long) BUFFER_SIZE.get(segment());
    }

    private void setBufferSize(long size) {
        BUFFER_SIZE.set(segment(), size);
    }

    private void setRemoteKey(MemorySegment remoteKey) {
        final var keySize = remoteKey.byteSize();
        if (keySize > KEY_DATA_SIZE) {
            throw new IllegalArgumentException("Remote key does not fit into buffer");
        }

        segment().asSlice(KEY_DATA_OFFSET).copyFrom(remoteKey);
    }

    public long remoteSize() {
        return (long) BUFFER_SIZE.get(segment());
    }

    public MemoryAddress remoteAddress() {
        return getBufferAddress();
    }

    MemorySegment keySegment() {
        return segment().asSlice(KEY_DATA_OFFSET);
    }
}
