package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public class MemoryDescriptor extends NativeObject {

    private static final long SIZE = 64L;

    private static final long OFFSET_BUFFER_ADDRESS = 0L;
    private static final long OFFSET_BUFFER_SIZE = OFFSET_BUFFER_ADDRESS + Long.BYTES;
    private static final long OFFSET_KEY_SIZE = OFFSET_BUFFER_SIZE + Long.BYTES;
    private static final long OFFSET_KEY_DATA = OFFSET_KEY_SIZE + Long.BYTES;

    public MemoryDescriptor() {
        super(MemorySegment.allocateNative(SIZE));
    }

    MemoryDescriptor(MemorySegment segment, MemorySegment remoteKey) {
        super(MemorySegment.allocateNative(SIZE));

        setBufferAddress(segment.address());
        setBufferSize(segment.byteSize());
        setRemoteKey(remoteKey);
    }

    private void setBufferAddress(MemoryAddress address) {
        MemoryAccess.setAddressAtOffset(segment(), OFFSET_BUFFER_ADDRESS, address);
    }

    private void setBufferSize(long size) {
        MemoryAccess.setLongAtOffset(segment(), OFFSET_BUFFER_SIZE, size);
    }

    private void setRemoteKey(MemorySegment remoteKey) {
        final var keySize = remoteKey.byteSize();
        if (keySize > SIZE - OFFSET_KEY_DATA) {
            throw new IllegalArgumentException("Remote key does not fit into buffer");
        }

        MemoryAccess.setLongAtOffset(segment(), OFFSET_KEY_SIZE, keySize);
        segment().asSlice(OFFSET_KEY_DATA).copyFrom(remoteKey);
    }

    public long remoteSize() {
        return MemoryAccess.getLongAtOffset(segment(), OFFSET_BUFFER_SIZE);
    }

    public MemoryAddress remoteAddress() {
        return MemoryAccess.getAddressAtOffset(segment(), OFFSET_BUFFER_ADDRESS);
    }

    MemorySegment keySegment() {
        var keySize = MemoryAccess.getLongAtOffset(segment(), OFFSET_KEY_SIZE);
        return segment().asSlice(OFFSET_KEY_DATA, keySize);
    }
}
