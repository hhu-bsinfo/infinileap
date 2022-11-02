package de.hhu.bsinfo.infinileap.engine.channel;

import de.hhu.bsinfo.infinileap.binding.Identifier;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import de.hhu.bsinfo.infinileap.engine.buffer.PooledBuffer;
import de.hhu.bsinfo.infinileap.engine.event.loop.EventLoopContext;
import de.hhu.bsinfo.infinileap.engine.event.message.SendActiveMessage;
import de.hhu.bsinfo.infinileap.engine.message.Callback;
import de.hhu.bsinfo.infinileap.engine.buffer.StaticBufferPool;
import lombok.extern.slf4j.Slf4j;
import org.agrona.hints.ThreadHints;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

@Slf4j
public class Channel {

    private final int identifier;

    private final RingBuffer ringBuffer;

    /**
     * The context belonging to the event loop this channel is attached to.
     */
    private final EventLoopContext context;

    public Channel(int identifier, RingBuffer ringBuffer, EventLoopContext context) {
        this.identifier = identifier;
        this.ringBuffer = ringBuffer;
        this.context = context;
    }

    public int identifier() {
        return identifier;
    }

    public void send(Identifier identifier, PooledBuffer buffer, int length, Callback<Void> callback) {

        // Set callback for completion event
        buffer.setCallback(callback);

        // Send active messages directly if we are inside the event loop
        if (context.isInEventloop()) {
            context.getLoop().sendActive(
                    this.identifier,
                    buffer.identifier(),
                    identifier.value(),
                    length
            );

            return;
        }

        MemorySegment segment;
        while ((segment = ringBuffer.claim(SendActiveMessage.BYTES, 100)) == null) {
            try {
                context.getLoopNotifier().fire();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Send message to event loop
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

    public PooledBuffer claimBuffer() {
        PooledBuffer buffer;
        while ((buffer = context.claimBuffer()) == null) {
            try {
                context.getLoopNotifier().fire();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return buffer;
    }
}
