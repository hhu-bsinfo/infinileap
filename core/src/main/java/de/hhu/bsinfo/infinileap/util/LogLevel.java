package de.hhu.bsinfo.infinileap.util;

import static org.openucx.OpenUcx.*;

public enum LogLevel {
    TRACE(UCS_LOG_LEVEL_FATAL()),
    DEBUG(UCS_LOG_LEVEL_DEBUG()),
    DIAG(3),
    INFO(UCS_LOG_LEVEL_INFO()),
    WARN(UCS_LOG_LEVEL_WARN()),
    ERROR(UCS_LOG_LEVEL_ERROR()),
    FATAL(UCS_LOG_LEVEL_FATAL());

    private final int level;

    LogLevel(int level) {
        this.level = level;
    }

    public static LogLevel from(int level) {
        for (var value : values()) {
            if (value.level == level) {
                return value;
            }
        }

        throw new IllegalArgumentException("Level " + level + " not defined");
    }
}
