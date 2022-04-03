package de.hhu.infinileap.engine.util;

import de.hhu.bsinfo.infinileap.binding.Endpoint;
import de.hhu.infinileap.engine.channel.Channel;
import jdk.incubator.foreign.MemoryAddress;

@FunctionalInterface
public interface ChannelResolver {
    Channel resolve(MemoryAddress address);
}
