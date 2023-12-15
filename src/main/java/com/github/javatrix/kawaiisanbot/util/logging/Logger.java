package com.github.javatrix.kawaiisanbot.util.logging;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.github.javatrix.kawaiisanbot.util.logging.TerminalColor.*;

public class Logger {

    private String name;
    private String logPattern = "[{name} {hour}:{minute}:{second}]({type}): {message}";
    private final Map<LogType, Boolean> disabled = new HashMap<>();
    private final Map<LogParameters, TerminalColor> colors = new HashMap<>();

    public Logger(String name) {
        this.name = name;
        setDisabled(LogType.DEBUG, true);

        setColor(LogParameters.MESSAGE, WHITE_BRIGHT);
        setColor(LogParameters.TYPE, WHITE_BRIGHT);
        setColor(LogParameters.NAME, MAGENTA_BRIGHT);
        setColor(LogParameters.HOUR, RED_BRIGHT);
        setColor(LogParameters.MINUTE, RED_BRIGHT);
        setColor(LogParameters.SECOND, RED_BRIGHT);
    }

    public void info(String message) {
        log(LogType.INFO, message);
    }

    public void warning(String message) {
        log(LogType.WARNING, message);
    }

    public void error(String message) {
        log(LogType.ERROR, LogType.ERROR.getColor() + message);
    }

    public void debug(String message) {
        log(LogType.DEBUG, message);
    }

    public void exception(Exception ex) {
        error(ex.getClass().getCanonicalName() + ": " + ex.getLocalizedMessage());
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            stackTrace.append("\t").append(element).append("\n");
        }
        error(stackTrace.toString());
    }

    public void log(LogType type, String message) {
        if (isDisabled(type)) {
            return;
        }
        System.out.println(format(type, message));
    }

    private String format(LogType type, String message) {
        String hour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
        String second = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
        return logPattern
                .replace(LogParameters.MESSAGE.toString(), colors.get(LogParameters.MESSAGE) + message + RESET)
                .replace(LogParameters.NAME.toString(), colors.get(LogParameters.NAME) + name + RESET)
                .replace(LogParameters.TYPE.toString(), colors.get(LogParameters.TYPE) + type.toString() + RESET)
                .replace(LogParameters.HOUR.toString(), colors.get(LogParameters.HOUR) + hour + RESET)
                .replace(LogParameters.MINUTE.toString(), colors.get(LogParameters.MINUTE) + minute + RESET)
                .replace(LogParameters.SECOND.toString(), colors.get(LogParameters.SECOND) + second + RESET);
    }

    public boolean isDisabled(LogType type) {
        return disabled.get(type) != null && disabled.get(type);
    }

    public void setDisabled(LogType type, boolean disabled) {
        this.disabled.put(type, disabled);
    }

    public void enable(LogType type) {
        disabled.put(type, false);
    }

    public void disable(LogType type) {
        disabled.put(type, true);
    }

    public void setColor(LogParameters parameter, TerminalColor terminalColor) {
        colors.put(parameter, terminalColor);
    }

    public TerminalColor getColor(LogParameters parameter) {
        return colors.get(parameter);
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

    public enum LogParameters {
        NAME,
        HOUR,
        MINUTE,
        SECOND,
        TYPE,
        MESSAGE;

        @Override
        public String toString() {
            return "{" + name().toLowerCase() + "}";
        }
    }

}
