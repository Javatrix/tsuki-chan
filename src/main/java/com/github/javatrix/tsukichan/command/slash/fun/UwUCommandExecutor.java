/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash.fun;

import com.github.javatrix.tsukichan.command.slash.SlashCommandExecutor;
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
