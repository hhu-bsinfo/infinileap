package de.hhu.bsinfo.infinileap.benchmark.context.buffer;

import de.hhu.bsinfo.infinileap.binding.ControlException;
import de.hhu.bsinfo.infinileap.common.buffer.ManyToOneRingBuffer;
import de.hhu.bsinfo.infinileap.common.buffer.RingBuffer;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Control;

import java.lang.foreign.MemorySegment;


@Slf4j
@State(Scope.Group)
public class AgronaSegmentContext {

    private static final int MESSAGE_ID = 42;

    private static final int BUFFER_SIZE = 64;

    private static final int RINGBUFFER_SIZE = 4096 * 1024;

    private static final int MESSAGE_LIMIT = RINGBUFFER_SIZE / BUFFER_SIZE;

    private ManyToOneRingBuffer ringBuffer;

    @Setup(Level.Iteration)
    public void setup() throws ControlException, InterruptedException {
        ringBuffer = new ManyToOneRingBuffer(RINGBUFFER_SIZE);
        readCounter = 0;
    }

    private int readCounter = 0;

    private final ManyToOneRingBuffer.MessageHandler handler = (msgTypeId, buffer, index, length) -> {
        readCounter++;
    };


    public final void read(Control control) {
        // Read <MESSAGE_LIMIT> messages from the ring buffer.
        while (readCounter != MESSAGE_LIMIT && !control.stopMeasurement) {
            ringBuffer.read(handler, MESSAGE_LIMIT - readCounter);
        }

        // Reset the counter
        readCounter = 0;
    }

    public final void write(Control control) {
        long index;
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
