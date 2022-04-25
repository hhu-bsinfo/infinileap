package de.hhu.bsinfo.infinileap.common.memory;

public enum MemoryAlignment {
    TYPE    (0x0008),
    CACHE   (0x0040),
    PAGE    (0x1000);

    private final int alignment;

    MemoryAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int value() {
        return alignment;
    }
}
