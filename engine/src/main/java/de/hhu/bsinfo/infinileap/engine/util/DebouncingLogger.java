package de.hhu.bsinfo.infinileap.engine.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DebouncingLogger {

    private long lastPrintTime;

    private final int debounceTime;

    public DebouncingLogger(int debounceTime) {
        this.debounceTime = debounceTime;
    }

    public void info(String message, Object... arguments) {
        if ((System.currentTimeMillis() - lastPrintTime) > debounceTime) {
            log.info(message, arguments);
            lastPrintTime = System.currentTimeMillis();
        }
    }

    public void error(String message, Object... arguments) {
        if ((System.currentTimeMillis() - lastPrintTime) > debounceTime) {
            log.error(message, arguments);
            lastPrintTime = System.currentTimeMillis();
        }
    }
}
