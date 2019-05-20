package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.ReferenceFactory;
import de.hhu.bsinfo.neutrino.util.ValueFactory;
import java.nio.ByteBuffer;

public final class Nested {

    private Nested() {}

    public static <T extends Struct> T byValue(final ByteBuffer byteBuffer, int offset, ValueFactory<T> factory) {
        return factory.newInstance(byteBuffer, offset);
    }

    public static <T extends Struct> T byReference(final ByteBuffer byteBuffer, int offset, ReferenceFactory<T> factory) {
        return factory.newInstance(byteBuffer.getLong(offset));
    }
}
