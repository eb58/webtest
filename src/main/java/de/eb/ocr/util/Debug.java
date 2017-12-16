package de.eb.ocr.util;

import org.apache.log4j.Logger;

public class Debug {

    static Logger log = Logger.getRootLogger();

    public static void error(String format, Object... o) {
        String msg = String.format(format, o);
        log.error(msg);
    }

    public static void info(String format, Object... o) {
        String msg = String.format(format, o);
        log.info(msg);
    }

    public static void warn(String format, Object... o) {
        String msg = String.format(format, o);
        log.warn(msg);
    }

    public static void debug(String format, Object... o) {
        String msg = String.format(format, o);
        log.debug(msg);
    }
}
