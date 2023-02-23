package de.hhu.bsinfo.infinileap.common.multiplex;

import de.hhu.bsinfo.infinileap.common.io.FileDescriptor;
import de.hhu.bsinfo.infinileap.common.memory.MemoryUtil;
import de.hhu.bsinfo.infinileap.common.util.BitMask;
import de.hhu.bsinfo.infinileap.common.util.NativeError;
import de.hhu.bsinfo.infinileap.common.util.flag.IntegerFlag;
import org.unix.Linux;
import org.unix.itimerspec;
import org.unix.timespec;

import java.io.IOException;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.time.Duration;

import static org.unix.Linux.*;

public final class TimerFileDescriptor extends FileDescriptor {

    private final MemorySession session = MemorySession.openImplicit();

    private final MemorySegment counter = MemorySegment.allocateNative(ValueLayout.JAVA_LONG, session);

    private static final int COUNTER_SIZE = 8;

    private TimerFileDescriptor(int fd) {
        super(fd);
    }

    public void reset() throws IOException {
        read();
    }

    public long read() throws IOException {
        var bytesRead = Linux.read(intValue(), counter, COUNTER_SIZE);
        if (bytesRead == NativeError.ERROR) {
            throw new IOException(NativeError.getMessage());
        }

        return counter.get(ValueLayout.JAVA_LONG, 0L);
    }

    public void set(Duration interval, Duration initialExpiration, TimerFlag... timerFlags) throws IOException {
        try (var session = MemorySession.openConfined()) {
            var timerSpecification = itimerspec.allocate(session);

            // Set interval
            var nativeInterval = itimerspec.it_interval$slice(timerSpecification);
            timespec.tv_sec$set(nativeInterval, interval.getSeconds());
            timespec.tv_nsec$set(nativeInterval, interval.getNano());

            // Set initial expiration
            var nativeExpiration = itimerspec.it_value$slice(timerSpecification);
            timespec.tv_sec$set(nativeExpiration, initialExpiration.getSeconds());
            timespec.tv_nsec$set(nativeExpiration, initialExpiration.getNano());

            if (timerfd_settime(
                    intValue(),
                    BitMask.intOf(timerFlags),
                    timerSpecification,
                    MemoryAddress.NULL) != NativeError.OK
            ) {
                throw new IOException(NativeError.getMessage());
            }
        }
    }

    public static TimerFileDescriptor create(OpenMode... openModes) throws IOException {
        return create(ClockType.MONOTONIC, openModes);
    }

    public static TimerFileDescriptor create(ClockType clockType, OpenMode... openModes) throws IOException {
        var flags = BitMask.intOf(openModes);
        var descriptor = timerfd_create(clockType.getValue(), flags);
        if (descriptor == NativeError.ERROR) {
            throw new IOException(NativeError.getMessage());
        }

        return new TimerFileDescriptor(descriptor);
    }

    public enum ClockType {
        REALTIME(CLOCK_REALTIME()),
        MONOTONIC(CLOCK_MONOTONIC()),
        BOOTTIME(CLOCK_BOOTTIME()),
        REALTIME_ALARM(CLOCK_REALTIME_ALARM()),
        BOOTTIME_ALARM(CLOCK_BOOTTIME_ALARM());

        private final int value;

        ClockType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum OpenMode implements IntegerFlag {
        NONBLOCK(TFD_NONBLOCK()),
        CLOEXEC(TFD_CLOEXEC());

        private final int value;

        OpenMode(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }

    public enum TimerFlag implements IntegerFlag {
        ABSOLUTE_TIME(TFD_TIMER_ABSTIME()),
        CANCEL_ON_SET(TFD_TIMER_CANCEL_ON_SET());

        private final int value;

        TimerFlag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
