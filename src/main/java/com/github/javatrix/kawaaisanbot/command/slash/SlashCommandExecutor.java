package com.github.javatrix.kawaaisanbot.command.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandExecutor {

    void process(SlashCommandInteractionEvent context);

}
