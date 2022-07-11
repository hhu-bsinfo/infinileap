package de.hhu.bsinfo.infinileap.engine.message;

import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import jdk.incubator.foreign.MemorySegment;

public interface MessageHandler {

    void onMessage(MessageLite message, Channel channel);
}
