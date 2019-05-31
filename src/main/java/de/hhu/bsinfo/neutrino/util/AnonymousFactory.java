package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.data.NativeObject;
import java.nio.ByteBuffer;

@FunctionalInterface
public interface AnonymousFactory<T extends NativeObject> {
    T newInstance(LocalBuffer buffer);
}
