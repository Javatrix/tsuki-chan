package com.github.javatrix.kawaiisanbot.command.slash;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.TimeUnit;

public class TempBanExecutor implements SlashCommandExecutor {

    public static final OptionData USER_OPTION = new OptionData(OptionType.USER, "user", "The user to tempban.", true);
    public static final OptionData REASON_OPTION = new OptionData(OptionType.STRING, "reason", "A reason for the ban.");
    public static final OptionData TIME_OPTION = new OptionData(OptionType.NUMBER, "time", "Time for which to tempban.", true);
    public static final OptionData TIME_UNIT_OPTION = new OptionData(OptionType.STRING, "time_unit", "Time for which to tempban.", true)
            .addChoice("seconds", "Seconds")
            .addChoice("minutes", "Minutes")
            .addChoice("hours", "Hours")
            .addChoice("days", "Days");

    @Override
    public void process(SlashCommandInteractionEvent context) {
        User user = context.getOption(USER_OPTION.getName()).getAsUser();
        String reason = context.getOption(REASON_OPTION.getName()) == null ? "Ban hammer has spoken!" : context.getOption(REASON_OPTION.getName()).getAsString();
        double time = context.getOption(TIME_OPTION.getName()).getAsDouble();
        TimeUnit unit = TimeUnit.valueOf(context.getOption(TIME_UNIT_OPTION.getName()).getAsString().toUpperCase());

        context.getGuild().ban(user, 0, TimeUnit.SECONDS).reason(reason).queue();
        context.reply(user.getName() + " was banned. They will be able to join again after " + time + " " + unit.name().toLowerCase() + ".").setEphemeral(true).queue();
    }
}
