package de.hhu.bsinfo.infinileap.engine.channel;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import de.hhu.bsinfo.infinileap.engine.util.BufferPool;
import de.hhu.bsinfo.infinileap.common.util.Distributable;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import jdk.incubator.foreign.ValueLayout;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openucx.Communication.ucp_request_free;

public class Channel {

    private static final int REQUEST_SHIFT = Integer.SIZE;

    private final int identifier;

    private final RingBuffer ringBuffer;

    public Channel(int identifier, RingBuffer ringBuffer) {
        this.identifier = identifier;
        this.ringBuffer = ringBuffer;
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

    public void send(Identifier identifier, Distributable header, Distributable body, Callback<Void> callback) {
        var segment= ringBuffer.claim((int) header.byteSize() + 2 * Integer.BYTES);
        segment.set(ValueLayout.JAVA_INT, 0, this.identifier);
        segment.set(ValueLayout.JAVA_INT, Integer.BYTES, identifier.value());
        header.writeTo(segment.asSlice(2 * Integer.BYTES));
        ringBuffer.commitWrite(segment);

        try {
            ringBuffer.wake();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
