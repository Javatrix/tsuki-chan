package com.github.javatrix.kawaiisanbot.util.logging;

import java.util.Calendar;

public class Logger {

    private String name;
    private String logPattern = "[{name} {hour}:{minute}:{second}]({type}): {message}";

    public Logger(String name) {
        this.name = name;
    }

    public void info(String message) {
        log(LogType.INFO, message);
    }

    public void warning(String message) {
        log(LogType.WARNING, message);
    }

    public void error(String message) {
        log(LogType.ERROR, message);
    }

    public void debug(String message) {
        log(LogType.DEBUG, message);
    }

    public void log(LogType type, String message) {
        System.out.println(format(type, message));
    }

    private String format(LogType type, String message) {
        String hour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
        String second = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
        return logPattern
                .replace(Parameters.MESSAGE, message)
                .replace(Parameters.NAME, name)
                .replace(Parameters.TYPE, type.name())
                .replace(Parameters.HOUR, hour)
                .replace(Parameters.MINUTE, minute)
                .replace(Parameters.SECOND, second);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogPattern() {
        return logPattern;
    }

    public void setLogPattern(String logPattern) {
        this.logPattern = logPattern;
    }

    public static final class Parameters {
        public static final String NAME = "{name}";
        public static final String HOUR = "{hour}";
        public static final String MINUTE = "{minute}";
        public static final String SECOND = "{second}";
        public static final String TYPE = "{type}";
        public static final String MESSAGE = "{message}";
    }

}
