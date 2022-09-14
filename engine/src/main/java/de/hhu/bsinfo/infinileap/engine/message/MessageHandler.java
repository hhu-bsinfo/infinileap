package de.hhu.bsinfo.infinileap.engine.message;

import de.hhu.bsinfo.infinileap.engine.channel.Channel;

import java.lang.foreign.MemorySegment;

public interface MessageHandler {

    void onMessage(MemorySegment header, MemorySegment body, Channel channel);
}
