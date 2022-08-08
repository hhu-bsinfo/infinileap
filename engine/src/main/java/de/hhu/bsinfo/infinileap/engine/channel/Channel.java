package de.hhu.bsinfo.infinileap.engine.channel;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import de.hhu.bsinfo.infinileap.engine.agent.message.SendActiveMessage;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import de.hhu.bsinfo.infinileap.common.util.Distributable;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.engine.util.MemorySegmentOutputStream;
import de.hhu.bsinfo.infinileap.message.TextMessage;
import java.lang.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openucx.Communication.ucp_request_free;

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

    public void send(Identifier identifier, Message message, Callback<Void> callback) {

        // Copy data into reserved buffer
        var buffer = bufferPool.claim();

        // Allocate output stream pointing to claimed buffer region
        var outputStream = MemorySegmentOutputStream.wrap(buffer.segment());

        try {
            // Write message to claimed buffer region
            message.writeTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Remember number of bytes written
        var length = outputStream.bytesWritten();

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
}
