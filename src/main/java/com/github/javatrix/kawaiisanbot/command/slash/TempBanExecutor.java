/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/kawaiisanbot/main/LICENSE
 */

package com.github.javatrix.kawaiisanbot.command.slash;

import com.github.javatrix.kawaiisanbot.KawaiiSan;
import com.github.javatrix.kawaiisanbot.util.MemberUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TempBanExecutor implements SlashCommandExecutor {

    public static final OptionData USER_OPTION = new OptionData(OptionType.USER, "user", "The user to tempban.", true);
    public static final OptionData REASON_OPTION = new OptionData(OptionType.STRING, "reason", "A reason for the ban.");
    public static final OptionData TIME_OPTION = new OptionData(OptionType.INTEGER, "time", "Time for which to tempban.", true);
    public static final OptionData TIME_UNIT_OPTION = new OptionData(OptionType.STRING, "time_unit", "Time for which to tempban.", true)
            .addChoice("seconds", "Seconds")
            .addChoice("minutes", "Minutes")
            .addChoice("hours", "Hours")
            .addChoice("days", "Days");

    @Override
    public void process(SlashCommandInteractionEvent context) {
        User user = context.getOption(USER_OPTION.getName()).getAsUser();
        String reason = context.getOption(REASON_OPTION.getName()) == null ? "The ban hammer has spoken!" : context.getOption(REASON_OPTION.getName()).getAsString();
        int time = context.getOption(TIME_OPTION.getName()).getAsInt();
        TimeUnit unit = TimeUnit.valueOf(context.getOption(TIME_UNIT_OPTION.getName()).getAsString().toUpperCase());

        Date expirationDate = new Date(Calendar.getInstance().getTimeInMillis() + TimeUnit.MILLISECONDS.convert(time, unit));
        Member member = context.getGuild().retrieveMemberById(user.getId()).complete();
        if (!MemberUtils.canModify(KawaiiSan.getInstance().asMember(context.getGuild()), member)) {
            context.reply("Sorry, I couldn't fulfill your request. :confounded: The person you are trying to punish has a higher rank than I do.").setEphemeral(true).queue();
            return;
        }
        MemberUtils.tempban(member, expirationDate, reason);
        context.reply(user.getEffectiveName() + " was banned. They will be able to join again " + TimeFormat.DATE_TIME_LONG.format(expirationDate.getTime()) + ".").setEphemeral(true).queue();
        member.getUser().openPrivateChannel().complete().sendMessage("You have been banned from " + context.getGuild().getName() + " :( Your ban expires " + TimeFormat.DATE_TIME_LONG.format(expirationDate.getTime())).queue();
    }
}
