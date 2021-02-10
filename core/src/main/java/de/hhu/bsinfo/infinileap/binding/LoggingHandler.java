package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.LogLevel;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h;
import org.openucx.ucx_h.ucs_log_func_t;

import static org.openucx.ucx_h.ucs_log_get_buffer_size;
import static org.openucx.ucx_h.vsnprintf;

public interface LoggingHandler extends ucs_log_func_t {

    long LOG_BUFFER_SIZE = ucs_log_get_buffer_size();

    enum Action {
        STOP(ucx_h.UCS_LOG_FUNC_RC_STOP()),
        CONTINUE(ucx_h.UCS_LOG_FUNC_RC_CONTINUE());

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
        try (var buffer = MemorySegment.allocateNative(LOG_BUFFER_SIZE + 1)) {
            vsnprintf(buffer, buffer.byteSize(), format, arguments);
            return CLinker.toJavaString(buffer);
        }
    }
}
