package de.hhu.infinileap.engine.util;

import de.hhu.bsinfo.infinileap.binding.Endpoint;
import jdk.incubator.foreign.MemoryAddress;

@FunctionalInterface
public interface EndpointResolver {
    Endpoint resolve(MemoryAddress address);
}
