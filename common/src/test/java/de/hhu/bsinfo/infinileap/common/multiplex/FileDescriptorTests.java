package de.hhu.bsinfo.infinileap.common.multiplex;

import de.hhu.bsinfo.infinileap.common.multiplex.TimerFileDescriptor.OpenMode;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileDescriptorTests {

    private static final int TIMEOUT = 5;

    @Test
    public void testEventFileDescriptor() throws Exception {
        try (
            var eventFileDescriptor = EventFileDescriptor.create(EventFileDescriptor.OpenMode.NONBLOCK);
            var executor = Executors.newSingleThreadExecutor()
        ) {
            var epoll = Epoll.create();
            epoll.add(eventFileDescriptor, EventType.EPOLLIN, EventType.EPOLLET);

            var future = executor.submit(() -> {
                epoll.wait(Duration.ofSeconds(TIMEOUT));
                eventFileDescriptor.reset();
                return true;
            });

            eventFileDescriptor.fire();
            assertEquals(future.get(TIMEOUT, TimeUnit.SECONDS), true);
        }
    }

    @Test
    public void testTimerFileDescriptor() throws Exception {
        try (
            var executor = Executors.newSingleThreadExecutor()
        ) {
            var future = executor.submit(() -> {
                var epoll = Epoll.create();
                var timerFileDescriptor = TimerFileDescriptor.create(OpenMode.NONBLOCK, OpenMode.CLOEXEC);
                epoll.add(timerFileDescriptor, EventType.EPOLLIN, EventType.EPOLLET);

                var then = System.nanoTime();

                timerFileDescriptor.set(Duration.ZERO, Duration.ofSeconds(1));
                epoll.wait(Duration.ofSeconds(TIMEOUT));
                assertEquals(timerFileDescriptor.read(), 1);

                timerFileDescriptor.set(Duration.ZERO, Duration.ofSeconds(1));
                epoll.wait(Duration.ofSeconds(TIMEOUT));
                assertEquals(timerFileDescriptor.read(), 1);

                return Duration.ofNanos(System.nanoTime() - then);
            });

            var minimalDuration = Duration.ofSeconds(1);
            assertTrue(future.get(TIMEOUT, TimeUnit.SECONDS).compareTo(minimalDuration) > 0);
        }
    }
}
