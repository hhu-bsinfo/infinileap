package de.hhu.bsinfo.infinileap.engine.pipeline;

import de.hhu.bsinfo.infinileap.engine.channel.Channel;

import java.lang.foreign.MemorySegment;

public interface MessageProcessor {
    void process(Channel channel, int identifier, MemorySegment header, MemorySegment body);
}
