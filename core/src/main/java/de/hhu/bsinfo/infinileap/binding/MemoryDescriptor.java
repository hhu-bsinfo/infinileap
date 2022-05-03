package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import jdk.incubator.foreign.*;

import java.lang.invoke.VarHandle;

public class MemoryDescriptor extends NativeObject {

    private static final long KEY_DATA_SIZE = 64;

    private static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_LONG.withName("buf_size"),
            ValueLayout.ADDRESS.withName("buf_addr"),
            MemoryLayout.sequenceLayout(KEY_DATA_SIZE, ValueLayout.JAVA_CHAR).withName("key_data")
    );

    private static final VarHandle BUFFER_SIZE =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("buf_size"));

    private static final VarHandle BUFFER_ADDRESS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("buf_addr"));

    private static final long KEY_DATA_OFFSET =
            LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("key_data"));

    public MemoryDescriptor() {
        this(ResourceScope.newImplicitScope());
    }

    public MemoryDescriptor(ResourceScope scope) {
        super(MemorySegment.allocateNative(LAYOUT, scope));
    }

    MemoryDescriptor(MemorySegment segment, MemorySegment remoteKey) {
        this(segment, remoteKey, ResourceScope.newImplicitScope());
    }

    MemoryDescriptor(MemorySegment segment, MemorySegment remoteKey, ResourceScope scope) {
        super(MemorySegment.allocateNative(LAYOUT, scope));

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
