package com.github.javatrix.kawaiisanbot.util.logging;

public enum LogType {
    INFO(TerminalColor.BLUE),
    WARNING(TerminalColor.YELLOW_BRIGHT),
    ERROR(TerminalColor.RED_BRIGHT),
    DEBUG(TerminalColor.GREEN_BRIGHT);

    private final TerminalColor color;

    LogType(TerminalColor color) {
        this.color = color;
    }

    public TerminalColor getColor() {
        return color;
    }

    @Override
    public String toString() {
        return color + super.toString();
    }
}
