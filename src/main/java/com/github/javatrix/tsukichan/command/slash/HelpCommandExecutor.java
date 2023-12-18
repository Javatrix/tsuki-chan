/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash;

import com.github.javatrix.tsukichan.TsukiChan;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public class HelpCommandExecutor implements SlashCommandExecutor {

    @Override
    public void process(SlashCommandInteractionEvent context) {
        List<Command> commands = TsukiChan.getInstance().getApi().retrieveCommands().complete();
        StringBuilder helpPages = new StringBuilder();
        for (Command command : commands) {
            helpPages.append(command.getType() == Command.Type.SLASH ? "/" : "").append(command.getName());
            if (!command.getDescription().isEmpty()) {
                helpPages.append(": ").append(command.getDescription());
            }
            helpPages.append("\n");
        }
        context.reply(helpPages.toString()).setEphemeral(true).queue();
    }

}
