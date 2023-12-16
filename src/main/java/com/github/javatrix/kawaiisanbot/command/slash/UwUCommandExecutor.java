/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023 Javatrix.
 */

package com.github.javatrix.kawaiisanbot.command.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class UwUCommandExecutor implements SlashCommandExecutor {

    public static final OptionData SAY_UWU_OPTION = new OptionData(OptionType.BOOLEAN, "say_uwu", "Whether to say UwU or not.");

    @Override
    public void process(SlashCommandInteractionEvent context) {
        OptionMapping uwuOption = context.getOption(SAY_UWU_OPTION.getName());
        boolean sayUwU = true;
        if (uwuOption != null) {
            sayUwU = uwuOption.getAsBoolean();
        }

        context.reply(sayUwU ? "UwU!" : "No UwU today...?").queue();
    }

}
