/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.exception.command;

import com.github.javatrix.tsukichan.command.slash.SlashCommandExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandExecutionException extends Exception {
    public CommandExecutionException(SlashCommandInteractionEvent event, SlashCommandExecutor executor, Exception e) {
        super("Exception processing " + event + " by " + executor.getClass().getSimpleName() + ": " + e.getCause() + ": " + e.getLocalizedMessage());
    }
}
