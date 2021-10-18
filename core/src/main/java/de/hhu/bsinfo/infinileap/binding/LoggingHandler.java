package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.LogLevel;
import jdk.incubator.foreign.*;
import org.openucx.OpenUcx;
import org.openucx.ucs_log_func_t;

import static org.openucx.OpenUcx.ucs_log_get_buffer_size;
import static org.unix.Linux.vsnprintf;

public interface LoggingHandler extends ucs_log_func_t {

    long LOG_BUFFER_SIZE = ucs_log_get_buffer_size();

    enum Action {
        STOP(OpenUcx.UCS_LOG_FUNC_RC_STOP()),
        CONTINUE(OpenUcx.UCS_LOG_FUNC_RC_CONTINUE());

        private final int value;

        Action(int value) {
            this.value = value;
        }

        int value() {
            return value;
        }
    }

    Action logMessage(LogLevel level, String message);

    @Override
    default int apply(MemoryAddress file, int line, MemoryAddress function, int level, MemoryAddress config, MemoryAddress format, MemoryAddress arguments){
        return logMessage(LogLevel.from(level), formatMessage(format, arguments)).value();
    }


    private static String formatMessage(MemoryAddress format, MemoryAddress arguments) {
        try (var scope = ResourceScope.newConfinedScope()) {
            var buffer = MemorySegment.allocateNative(LOG_BUFFER_SIZE + 1, scope);
            vsnprintf(buffer, buffer.byteSize(), format, arguments);
            return buffer.getUtf8String(0L);
        }
    }
}
