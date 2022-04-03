package de.hhu.infinileap.engine.message;

import de.hhu.bsinfo.infinileap.binding.Endpoint;
import de.hhu.infinileap.engine.channel.Channel;
import jdk.incubator.foreign.MemorySegment;

public interface MessageHandler {

    void onMessage(MemorySegment header, MemorySegment body, Channel channel);
}
