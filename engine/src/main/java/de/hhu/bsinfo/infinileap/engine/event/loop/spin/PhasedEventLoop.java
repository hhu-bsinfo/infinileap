package de.hhu.bsinfo.infinileap.engine.event.loop.spin;

import de.hhu.bsinfo.infinileap.engine.event.loop.AbstractEventLoop;
import de.hhu.bsinfo.infinileap.engine.util.DebouncingLogger;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public abstract class PhasedEventLoop extends AbstractEventLoop {

    private final long yieldTimeout = Long.parseLong(System.getProperty("de.hhu.infinileap.loop.yieldTimeout", "3000"));
    private final long sleepTimeout = Long.parseLong(System.getProperty("de.hhu.infinileap.loop.sleepTimeout", "6000"));
    private final long sleepDuration = Long.parseLong(System.getProperty("de.hhu.infinileap.loop.sleepDuration", "1000"));

    private final DebouncingLogger logger = new DebouncingLogger(2000);

    public enum LoopStatus {
        ACTIVE, IDLE
    }

    private enum LoopMode {
        SPIN, YIELD, SLEEP
    }

    private long lastWorkTime = 0L;

    protected abstract LoopStatus doWork() throws Exception;

    @Override
    protected void onStart() throws Exception {
        super.onStart();
        lastWorkTime = System.currentTimeMillis();
    }

    @Override
    protected final void onLoop() throws Exception {

        // Fast path: loop is active, so we spin
        if (doWork() == LoopStatus.ACTIVE) {
//            logger.info("Spinning");
            lastWorkTime = System.currentTimeMillis();
            return;
        }

        switch (calculateMode()) {
            case YIELD -> {
//                logger.info("Yielding");
                Thread.yield();
            }

            case SLEEP -> {
//                logger.info("Sleeping");
                Thread.sleep(sleepDuration);
            }

            case SPIN -> {
                Thread.onSpinWait();
//                logger.info("Spinning");
            }
        }
    }

    private LoopMode calculateMode() {
        var idleDuration = System.currentTimeMillis() - lastWorkTime;

        if (idleDuration > sleepTimeout) {
            return LoopMode.SLEEP;
        }

        if (idleDuration > yieldTimeout) {
            return LoopMode.YIELD;
        }

        return LoopMode.SPIN;
    }

    private static long validateDuration(Duration duration, String name) {
        var millis = duration.toMillis();
        if (millis == 0) {
            throw new IllegalArgumentException(String.format("%s must be greater than 0.", name));
        }

        return millis;
    }
}
