package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.data.NativeObject;
import java.nio.ByteBuffer;

public interface ValueFactory<T extends NativeObject> {
    T newInstance(ByteBuffer byteBuffer, int offset);
}
