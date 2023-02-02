package de.hhu.bsinfo.infinileap.engine.channel;

import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import de.hhu.bsinfo.infinileap.engine.buffer.PooledBuffer;
import de.hhu.bsinfo.infinileap.engine.event.message.SendActiveMessage;

import java.lang.foreign.MemorySegment;

public class ChannelOperations {

    public static void appendSendActive(RingBuffer ringBuffer, int channelId, int messageId, PooledBuffer buffer, int length) {

        // Claim space within the event loop's ringbuffer
        MemorySegment segment = ringBuffer.claim(SendActiveMessage.BYTES);

        // Set fields
        SendActiveMessage.setChannelId(segment, channelId);
        SendActiveMessage.setMessageId(segment, messageId);
        SendActiveMessage.setBufferId(segment, buffer.identifier());
        SendActiveMessage.setLength(segment, length);

        // Commit written values
        ringBuffer.commitWrite(segment);
    }
}
