package com.github.javatrix.kawaiisanbot.command;

import com.github.javatrix.kawaiisanbot.command.slash.ClearChannelExecutor;
import com.github.javatrix.kawaiisanbot.command.slash.SelfRoleExecutor;
import com.github.javatrix.kawaiisanbot.command.slash.SlashCommandExecutor;
import com.github.javatrix.kawaiisanbot.command.slash.UwUCommandExecutor;
import com.github.javatrix.kawaiisanbot.command.user.HugCommandExecutor;
import com.github.javatrix.kawaiisanbot.command.user.UserCommandExecutor;
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

public class CommandManager extends ListenerAdapter {

    private final Map<String, SlashCommandExecutor> slashExecutors = new HashMap<>();
    private final Map<String, UserCommandExecutor> userExecutors = new HashMap<>();

    public CommandManager(JDA api) {
        api.updateCommands().addCommands(
                //Slash commands
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
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL))
        ).addCommands(
                //Context menu commands
                Commands.user("Hug")
        ).queue();

        slashExecutors.put("uwu", new UwUCommandExecutor());
        slashExecutors.put("selfrole", new SelfRoleExecutor());
        slashExecutors.put("clear", new ClearChannelExecutor());

        userExecutors.put("Hug", new HugCommandExecutor());

        api.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommandExecutor executor = slashExecutors.get(event.getName());
        if (executor == null) {
            event.reply("Sorry, it seems like the handler responsible for processing this command is not registered.Please report this issue to devs as soon as possible.").queue();
            return;
        }

        executor.process(event);
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
