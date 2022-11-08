package de.hhu.bsinfo.infinileap.engine.event.loop;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public abstract class PhasedEventLoop extends AbstractEventLoop {

    private final long yieldTimeout;
    private final long sleepTimeout;
    private final long sleepDuration;

    public enum LoopStatus {
        ACTIVE, IDLE
    }

    private enum LoopMode {
        YIELD, SLEEP
    }

    private long lastWorkTime = 0L;

    protected PhasedEventLoop(Duration yieldTimeout, Duration sleepTimeout, Duration sleepDuration) {
        this.yieldTimeout = validateDuration(yieldTimeout, "Yield timeout");
        this.sleepTimeout = validateDuration(sleepTimeout, "Sleep timeout");
        this.sleepDuration = validateDuration(sleepDuration, "Sleep duration");
    }

    protected abstract LoopStatus doWork() throws Exception;

    @Override
    protected final void onLoop() throws Exception {

        // Fast path: loop is active, so we spin
        if (doWork() == LoopStatus.ACTIVE) {
            log.info("Spinning");
            lastWorkTime = System.currentTimeMillis();
            return;
        }

        switch (calculateMode()) {
            case YIELD -> {
                log.info("Yielding");
                Thread.yield();
            }
            case SLEEP -> {
                log.info("Sleeping");
                Thread.sleep(sleepDuration);
            }
        }
    }

    private LoopMode calculateMode() {
        return System.currentTimeMillis() - lastWorkTime > sleepTimeout ?
                LoopMode.SLEEP :
                LoopMode.YIELD ;
    }

    private static long validateDuration(Duration duration, String name) {
        var millis = duration.toMillis();
        if (millis == 0) {
            throw new IllegalArgumentException(String.format("%s must be greater than 0.", name));
        }

        return millis;
    }
}
