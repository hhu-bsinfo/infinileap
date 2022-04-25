package de.hhu.bsinfo.infinileap.engine.util;

import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import jdk.incubator.foreign.MemoryAddress;

@FunctionalInterface
public interface ChannelResolver {
    Channel resolve(MemoryAddress address);
}
