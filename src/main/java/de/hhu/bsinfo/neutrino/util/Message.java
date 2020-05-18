package de.hhu.bsinfo.neutrino.util;

import org.slf4j.helpers.MessageFormatter;

public class Message {

    public static String format(String format, Object... args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
    }
}
