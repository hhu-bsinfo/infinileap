package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static de.hhu.bsinfo.infinileap.binding.LoggingHandler.Action.CONTINUE;
import static org.openucx.ucx_h.*;

public class NativeLogger {

    static {

        // Remove default log handler printing to stdout
        while (ucs_log_num_handlers() != 0) {
            ucs_log_pop_handler();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeLogger.class);

    private static final LoggingHandler HANDLER = (level, message) -> {
        switch (level) {
            case TRACE -> LOGGER.trace(message);
            case DEBUG -> LOGGER.debug(message);
            case INFO  -> LOGGER.info(message);
            case WARN  -> LOGGER.warn(message);
            case ERROR, FATAL -> LOGGER.error(message);
        }

        return CONTINUE;
    };

    private static final MemorySegment UPCALL = ucs_log_func_t.allocate(HANDLER);

    private static final AtomicBoolean ENABLED = new AtomicBoolean(false);

    public static void enable() {
        if (ENABLED.compareAndSet(false, true)) {
            ucs_log_push_handler(UPCALL);
        }
    }
}
