package com.github.javatrix.kawaaisanbot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class UwUCommand extends Command {

    private static final OptionData SAY_UWU_OPTION = new OptionData(OptionType.BOOLEAN, "say_uwu", "Whether to say UwU or not.");

    public UwUCommand() {
        super("uwu", "Says UwU or not, depending on the option chosen.", SAY_UWU_OPTION);
    }

    @Override
    public void execute(SlashCommandInteractionEvent context) {
        OptionMapping uwuOption = context.getOption(SAY_UWU_OPTION.getName());
        boolean sayUwU = false;
        if (uwuOption != null) {
            sayUwU = uwuOption.getAsBoolean();
        }

        context.reply(sayUwU ? "UwU!" : "No UwU today...?").queue();
    }

}