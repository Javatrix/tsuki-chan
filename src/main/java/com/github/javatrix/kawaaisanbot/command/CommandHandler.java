package com.github.javatrix.kawaaisanbot.command;

import com.github.javatrix.kawaaisanbot.command.slash.SlashCommandExecutor;
import com.github.javatrix.kawaaisanbot.command.slash.UwUCommandExecutor;
import com.github.javatrix.kawaaisanbot.command.user.HugCommandExecutor;
import com.github.javatrix.kawaaisanbot.command.user.UserCommandExecutor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {

    private final Map<String, SlashCommandExecutor> slashExecutors = new HashMap<>();
    private final Map<String, UserCommandExecutor> userExecutors = new HashMap<>();

    public CommandHandler(JDA api) {
        api.updateCommands().addCommands(
                //Slash commands
                Commands.slash("uwu", "Says UwU or not, depending on the option chosen.")
                        .addOptions(UwUCommandExecutor.SAY_UWU_OPTION)
        ).addCommands(
                //Context menu commands
                Commands.user("Hug")
        ).queue();

        slashExecutors.put("uwu", new UwUCommandExecutor());

        userExecutors.put("Hug", new HugCommandExecutor());

        api.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommandExecutor executor = slashExecutors.get(event.getFullCommandName());
        if (executor == null) {
            event.reply("Sorry, it seems like the handler responsible for processing this command is not registered.Please report this issue to devs as soon as possible.").queue();
            return;
        }

        executor.process(event);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        UserCommandExecutor executor = userExecutors.get(event.getFullCommandName());
        if (executor == null) {
            event.reply("Sorry, it seems like the handler responsible for processing this command is not registered.Please report this issue to devs as soon as possible.").queue();
            return;
        }

        executor.process(event);
    }
}
