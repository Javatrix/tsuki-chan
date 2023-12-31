/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command;

import com.github.javatrix.tsukichan.command.slash.HelpCommandExecutor;
import com.github.javatrix.tsukichan.command.slash.SlashCommandExecutor;
import com.github.javatrix.tsukichan.command.slash.fun.UwUCommandExecutor;
import com.github.javatrix.tsukichan.command.slash.moderation.SelfRoleExecutor;
import com.github.javatrix.tsukichan.command.slash.moderation.TempBanExecutor;
import com.github.javatrix.tsukichan.command.slash.music.PauseCommandExecutor;
import com.github.javatrix.tsukichan.command.slash.music.PlayCommandExecutor;
import com.github.javatrix.tsukichan.command.slash.music.QueueCommandExecutor;
import com.github.javatrix.tsukichan.command.slash.music.SkipCommandExecutor;
import com.github.javatrix.tsukichan.command.slash.utility.ClearChannelExecutor;
import com.github.javatrix.tsukichan.command.user.HugCommandExecutor;
import com.github.javatrix.tsukichan.command.user.UserCommandExecutor;
import com.github.javatrix.tsukichan.exception.command.CommandExecutionException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.github.javatrix.tsukichan.TsukiChan.LOGGER;

public class CommandManager extends ListenerAdapter {

    private final Map<String, SlashCommandExecutor> slashExecutors = new HashMap<>();
    private final Map<String, UserCommandExecutor> userExecutors = new HashMap<>();

    public CommandManager(JDA api) {
        api.updateCommands().addCommands(
                //Slash commands
                Commands.slash("help", "Shows this help."),
                Commands.slash("uwu", "Says UwU or not, depending on the option chosen.")
                        .addOptions(UwUCommandExecutor.SAY_UWU_OPTION),
                Commands.slash("selfrole", "Adds self roles to users.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES))
                        .addSubcommands(
                                new SubcommandData("add", "Add a role to the message that's gonna be sent.")
                                        .addOptions(new OptionData(OptionType.ROLE, "role", "Adds a role to the message.", true),
                                                new OptionData(OptionType.STRING, "emoji", "Emoji that will be displayed on the button.")),
                                new SubcommandData("remove", "Removes a role to the message that's gonna be sent.")
                                        .addOptions(new OptionData(OptionType.ROLE, "role", "Removes a role to the message.", true)),
                                new SubcommandData("list", "Lists all of your currently selected roles."),
                                new SubcommandData("send", "Send the message with selected roles.")
                        ),
                Commands.slash("clear", "Deletes all messages in the specific channel.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL)),
                Commands.slash("tempban", "Temporarily bans the specified user.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
                        .addOptions(
                                TempBanExecutor.USER_OPTION,
                                TempBanExecutor.TIME_OPTION,
                                TempBanExecutor.TIME_UNIT_OPTION,
                                TempBanExecutor.REASON_OPTION
                        ),
                Commands.slash("play", "Plays music in a voice channel.")
                        .addOptions(PlayCommandExecutor.TITLE_OPTION),
                Commands.slash("pause", "Pauses or resumes the paused song."),
                Commands.slash("skip", "Skips to the next song.")
                        .addOptions(SkipCommandExecutor.SKIP_COUNT_OPTION),
                Commands.slash("queue", "Lists all the songs in the queue.")
        ).addCommands(
                //Context menu commands
                Commands.user("Hug")
        ).queue();

        LOGGER.info("Creating slash executors...");
        slashExecutors.put("help", new HelpCommandExecutor());
        slashExecutors.put("uwu", new UwUCommandExecutor());
        slashExecutors.put("selfrole", new SelfRoleExecutor());
        slashExecutors.put("clear", new ClearChannelExecutor());
        slashExecutors.put("tempban", new TempBanExecutor());

        slashExecutors.put("play", new PlayCommandExecutor());
        slashExecutors.put("pause", new PauseCommandExecutor());
        slashExecutors.put("skip", new SkipCommandExecutor());
        slashExecutors.put("queue", new QueueCommandExecutor());

        LOGGER.info("Creating context menu executors...");
        userExecutors.put("Hug", new HugCommandExecutor());

        api.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        LOGGER.debug("Slash command: " + event.getInteraction().getFullCommandName() + " " + event.getInteraction().getOptions());
        SlashCommandExecutor executor = slashExecutors.get(event.getName());
        if (executor == null) {
            event.reply("Sorry, it seems like the handler responsible for processing this command is not registered.Please report this issue to devs as soon as possible.").queue();
            return;
        }

        try {
            executor.process(event);
        } catch (Exception e) {
            LOGGER.exception(new CommandExecutionException(event, executor, e));
        }
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        UserCommandExecutor executor = userExecutors.get(event.getName());
        if (executor == null) {
            event.reply("Sorry, it seems like the handler responsible for processing this command is not registered.Please report this issue to devs as soon as possible.").queue();
            return;
        }

        executor.process(event);
    }
}
