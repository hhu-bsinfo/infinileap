package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import org.openucx.ucx_h;

public enum MemoryType implements LongFlag {
    HOST(ucx_h.UCS_MEMORY_TYPE_HOST()),
    ROCM(ucx_h.UCS_MEMORY_TYPE_ROCM()),
    ROCM_MANAGED(ucx_h.UCS_MEMORY_TYPE_ROCM_MANAGED()),
    CUDA(ucx_h.UCS_MEMORY_TYPE_CUDA()),
    CUDA_MANAGED(ucx_h.UCS_MEMORY_TYPE_CUDA_MANAGED());

    private final long value;

    MemoryType(long value) {
        this.value = value;
    }

    @Override
    public long getValue() {
        return value;
    }
}
