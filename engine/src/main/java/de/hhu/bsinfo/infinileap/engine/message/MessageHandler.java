package de.hhu.bsinfo.infinileap.engine.message;

import com.google.protobuf.Message;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import jdk.incubator.foreign.MemorySegment;

public interface MessageHandler {

    void onMessage(Message message, Channel channel);
}
