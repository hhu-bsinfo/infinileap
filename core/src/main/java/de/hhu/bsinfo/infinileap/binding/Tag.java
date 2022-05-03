package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.common.util.flag.LongFlag;

public class Tag implements LongFlag {

    private final long value;

    private Tag(long value) {
        this.value = value;
    }

    public static Tag of(long value) {
        return new Tag(value);
    }

    @Override
    public long getValue() {
        return value;
    }
}
