package de.hhu.bsinfo.infinileap.benchmark.context.buffer;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Control;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;


@Slf4j
@State(Scope.Group)
public class InfinileapBufferContext {


    private static final int BUFFER_SIZE = 64;

    private static final int RINGBUFFER_SIZE = 4096 * 1024;

    private static final int NO_ELEMENT = 0;

    private static final int MESSAGE_LIMIT = RINGBUFFER_SIZE / BUFFER_SIZE;

    private RingBuffer ringBuffer;

    @Setup(Level.Iteration)
    public void setup() throws ControlException, InterruptedException {
        ringBuffer = new RingBuffer(RINGBUFFER_SIZE);
        readCounter = 0;
    }

    private int readCounter = 0;

    private final RingBuffer.MessageHandler handler = (msgTypeId, buffer, index, length) -> {
        readCounter++;
    };


    public final void read(Control control) {
        int bytesRead;
        while (readCounter != MESSAGE_LIMIT && !control.stopMeasurement) {
            bytesRead = ringBuffer.read(handler, MESSAGE_LIMIT - readCounter);
            if (bytesRead != NO_ELEMENT) {
                ringBuffer.commitRead(bytesRead);
            }
        }

        readCounter = 0;
    }

    public final void write(Control control) {
        MemorySegment segment;
        for (int i = 0; i < MESSAGE_LIMIT; i++) {

            // Wait until a chunk of memory is claimed.
            do {
                segment = ringBuffer.tryClaim(BUFFER_SIZE);
                if (control.stopMeasurement) {
                    return;
                }

            } while (segment == null);

            ringBuffer.commitWrite(segment);
        }
    }

}
