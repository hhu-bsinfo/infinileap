package de.hhu.bsinfo.infinileap.engine.channel;

import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import de.hhu.bsinfo.infinileap.engine.agent.message.SendActiveMessage;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.engine.pipeline.ChannelPipeline;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import de.hhu.bsinfo.infinileap.engine.util.MemorySegmentOutputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

@Slf4j
public class Channel {

    private final int identifier;

    private final BufferPool bufferPool;

    private final RingBuffer ringBuffer;

    public Channel(int identifier, RingBuffer ringBuffer, BufferPool bufferPool) {
        this.identifier = identifier;
        this.ringBuffer = ringBuffer;
        this.bufferPool = bufferPool;
    }

    public int identifier() {
        return identifier;
    }

    public void send(Identifier identifier, BufferPool.PooledBuffer buffer, int length, Callback<Void> callback) {

        // Set callback for completion event
        buffer.setCallback(callback);



        // Send message to event loop
        var segment= ringBuffer.claim(SendActiveMessage.BYTES);
        SendActiveMessage.setChannelId(segment, this.identifier);
        SendActiveMessage.setMessageId(segment, identifier.value());
        SendActiveMessage.setBufferId(segment, buffer.identifier());
        SendActiveMessage.setLength(segment, length);
        ringBuffer.commitWrite(segment);

        try {
            ringBuffer.wake();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferPool.PooledBuffer claimBuffer() {
        return bufferPool.claim();
    }
}
