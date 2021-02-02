package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;

import static org.openucx.ucx_h.*;

public class DataType {

    private static final int CLASS_SHIFT = UCP_DATATYPE_SHIFT();

    private static final DataType DATATYPE_IOV = new DataType(Type.IOV.value);

    static final DataType CONTIGUOUS_32_BIT = contiguous(4);
    static final DataType CONTIGUOUS_64_BIT = contiguous(8);

    private final long identifier;

    private DataType(long identifier) {
        this.identifier = identifier;
    }

    long identifier() {
        return identifier;
    }

    public static DataType contiguous(long size) {
        return new DataType(size << CLASS_SHIFT | Type.CONTIGIOUS.value);
    }

    public static DataType iov() {
        return DATATYPE_IOV;
    }

    enum Type implements IntegerFlag {
        CONTIGIOUS(UCP_DATATYPE_CONTIG()),
        STRIDED(UCP_DATATYPE_STRIDED()),
        IOV(UCP_DATATYPE_IOV()),
        GENERIC(UCP_DATATYPE_GENERIC());

        private final int value;

        Type(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
