package de.hhu.bsinfo.infinileap.engine.channel;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import de.hhu.bsinfo.infinileap.engine.agent.message.SendActiveMessage;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import de.hhu.bsinfo.infinileap.common.util.Distributable;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import jdk.incubator.foreign.ValueLayout;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openucx.Communication.ucp_request_free;

@Slf4j
public class Channel {

    private static final int REQUEST_SHIFT = Integer.SIZE;

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

    private static long pack(int requestIdentifier, int bufferIdentifier) {
        return (long) requestIdentifier << REQUEST_SHIFT | bufferIdentifier;
    }

    private static int getBufferIdentifier(long userData) {
        return (int) userData;
    }

    private static int getRequestIdentifier(long userData) {
        return (int) (userData >> REQUEST_SHIFT);
    }

    public void send(Identifier identifier, Distributable message, Callback<Void> callback) {

        // Copy data into reserved buffer
        log.debug("Claiming buffer");
        var buffer = bufferPool.claim();
        log.debug("Buffer {} claimed", buffer.identifier());

        var length = (int) message.writeTo(buffer.segment());
        buffer.setCallback(callback);

        // Send message to event loop

        log.debug("Claiming message");
        var segment= ringBuffer.claim(SendActiveMessage.BYTES);
        log.debug("Message {} claimed", ringBuffer.offsetOf(segment) / SendActiveMessage.BYTES);
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
