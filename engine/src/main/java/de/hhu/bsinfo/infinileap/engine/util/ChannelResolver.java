package de.hhu.bsinfo.infinileap.engine.util;

import de.hhu.bsinfo.infinileap.engine.channel.Channel;

@FunctionalInterface
public interface ChannelResolver {
    Channel resolve(long endpointAddress);
}
