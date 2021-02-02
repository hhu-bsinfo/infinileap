package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemorySegment;

class NativePrimitive extends NativeObject {

    private final DataType dataType;

    NativePrimitive(MemorySegment segment, DataType dataType) {
        super(segment);
        this.dataType = dataType;
    }

    public DataType dataType() {
        return dataType;
    }
}
