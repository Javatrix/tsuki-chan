/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash;

import com.github.javatrix.tsukichan.TsukiChan;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.awt.*;
import java.util.List;

public class HelpCommandExecutor implements SlashCommandExecutor {

    @Override
    public void process(SlashCommandInteractionEvent context) {
        List<Command> commands = TsukiChan.getInstance().getApi().retrieveCommands().complete();
        StringBuilder helpPages = new StringBuilder();
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(199, 7, 229, 255))
                .setDescription(helpPages)
                .setTitle("Commands list");
        for (Command command : commands) {
            embed.addField(command.getType() == Command.Type.SLASH ? "/" + command.getName() : command.getName(), command.getDescription().isEmpty() ? "No description." : command.getDescription(), false);
        }
        context.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

}
