package de.hhu.bsinfo.infinileap.engine.event.loop;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.common.multiplex.EventFileDescriptor;
import de.hhu.bsinfo.infinileap.common.multiplex.EventFileDescriptor.OpenMode;
import de.hhu.bsinfo.infinileap.common.multiplex.EventType;
import de.hhu.bsinfo.infinileap.common.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.util.ResourcePool;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpollEventLoopTest {

    private static final Integer ATTACHEMENT = 42;

    private static final int TIMEOUT = 5;

    @Test
    public void testEventFileDescriptor() throws Exception {

        final var latch = new CountDownLatch(1);
        var eventLoop = new EpollEventLoop<Integer>() {
            @Override
            protected void process(SelectionKey<Integer> selectionKey) throws IOException {
                assertEquals(selectionKey.attachment(), ATTACHEMENT);
                latch.countDown();
            }
        };

        // Start the event loop
        eventLoop.start(Thread::new);

        // Create event file descriptor
        var descriptor = EventFileDescriptor.create(OpenMode.NONBLOCK);
        eventLoop.add(descriptor, ATTACHEMENT, EventType.EPOLLIN, EventType.EPOLLET);

        // Write to event file descriptor and wait for loop to wake up
        descriptor.fire();
        assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
    }

    @Test
    public void testUcpWorker() throws Exception {
        var contextParameters = new ContextParameters()
                .setFeatures(ContextParameters.Feature.WAKEUP);

        var workerParameters = new WorkerParameters()
                .setThreadMode(ThreadMode.SINGLE);

        try (var pool = new ResourcePool()) {
            var context = pool.push(Context.initialize(contextParameters));
            var worker = pool.push(context.createWorker(workerParameters));

            final var latch = new CountDownLatch(1);
            var eventLoop = new EpollEventLoop<Integer>() {
                @Override
                protected void process(SelectionKey<Integer> selectionKey) throws IOException {
                    assertEquals(selectionKey.attachment(), ATTACHEMENT);

                    do {
                        worker.progress();
                    } while (worker.arm() != Status.OK);

                    latch.countDown();
                }
            };

            // Start the event loop
            eventLoop.start(Thread::new);

            // Create event file descriptor
            eventLoop.add(worker, ATTACHEMENT, EventType.EPOLLIN);

            // Write to event file descriptor and wait for loop to wake up
            worker.signal();
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }
    }
}
