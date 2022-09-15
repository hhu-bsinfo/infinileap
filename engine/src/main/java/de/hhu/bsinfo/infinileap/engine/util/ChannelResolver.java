package de.hhu.bsinfo.infinileap.engine.util;

import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import java.lang.foreign.MemoryAddress;

@FunctionalInterface
public interface ChannelResolver {
    Channel resolve(long endpointAddress);
}
