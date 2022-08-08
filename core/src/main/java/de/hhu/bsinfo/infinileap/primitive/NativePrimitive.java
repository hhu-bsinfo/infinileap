package de.hhu.bsinfo.infinileap.primitive;

import de.hhu.bsinfo.infinileap.binding.DataType;
import de.hhu.bsinfo.infinileap.common.util.NativeObject;
import java.lang.foreign.MemorySegment;

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
