/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/kawaiisanbot/main/LICENSE
 */

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
