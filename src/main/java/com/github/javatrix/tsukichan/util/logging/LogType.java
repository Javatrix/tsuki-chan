/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.util.logging;

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
