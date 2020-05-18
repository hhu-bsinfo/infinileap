package de.hhu.bsinfo.neutrino.util.factory;

import de.hhu.bsinfo.neutrino.struct.field.NativeObject;
import org.agrona.concurrent.AtomicBuffer;

@FunctionalInterface
public interface AnonymousFactory<T extends NativeObject> {
    T newInstance(AtomicBuffer buffer);
}
