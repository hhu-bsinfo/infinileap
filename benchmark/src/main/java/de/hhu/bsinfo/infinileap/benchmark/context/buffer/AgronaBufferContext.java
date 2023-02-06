package de.hhu.bsinfo.infinileap.benchmark.context.buffer;

import de.hhu.bsinfo.infinileap.benchmark.connection.BenchmarkClient;
import de.hhu.bsinfo.infinileap.benchmark.message.BenchmarkDetails;
import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.engine.util.DebouncingLogger;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.MessageHandler;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.infra.ThreadParams;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@Slf4j
@State(Scope.Group)
public class AgronaBufferContext {

    private static final int MESSAGE_ID = 42;

    private static final int BUFFER_SIZE = 64;

    private static final int RINGBUFFER_SIZE = 4096 * 1024;

    private static final int NO_ELEMENT = 0;

    private static final int MESSAGE_LIMIT = RINGBUFFER_SIZE / BUFFER_SIZE;

    private  RingBuffer ringBuffer;

    private int readCounter = 0;

    private final DebouncingLogger logger = new DebouncingLogger(1000);

    private final MessageHandler handler = (msgTypeId, buffer, index, length) -> {
        readCounter++;
    };

    @Setup(Level.Iteration)
    public void setup() throws ControlException, InterruptedException {
        ringBuffer = new ManyToOneRingBuffer(new UnsafeBuffer(ByteBuffer.allocateDirect(RINGBUFFER_SIZE + RingBufferDescriptor.TRAILER_LENGTH)));
        readCounter = 0;
    }


    public final void read(Control control) {
        // Read <MESSAGE_LIMIT> messages from the ring buffer.
        while (readCounter != MESSAGE_LIMIT && !control.stopMeasurement) {
            ringBuffer.read(handler, MESSAGE_LIMIT - readCounter);
        }

        // Reset the counter
        readCounter = 0;
    }

    public final void write(Control control) {
        int index;
        for (int i = 0; i < MESSAGE_LIMIT; i++) {

            // Wait until a chunk of memory is claimed.
            do {
                index = ringBuffer.tryClaim(MESSAGE_ID, BUFFER_SIZE);
                if (control.stopMeasurement) {
                    return;
                }

            } while (index < 0);

            // Commit the claimed memory.
            ringBuffer.commit(index);
        }
    }
}
