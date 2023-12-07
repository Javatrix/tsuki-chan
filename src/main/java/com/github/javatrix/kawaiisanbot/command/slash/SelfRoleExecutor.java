package com.github.javatrix.kawaiisanbot.command.slash;

import com.github.javatrix.kawaiisanbot.KawaiiSan;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SelfRoleExecutor implements SlashCommandExecutor {

    private static final Map<User, Set<Role>> roles = new HashMap<>();

    @Override
    public void process(SlashCommandInteractionEvent context) {
        switch (context.getSubcommandName()) {
            case "add" -> addRole(context);
            case "remove" -> removeRole(context);
            case "send" -> sendRoles(context);
        }
    }

    private void removeRole(SlashCommandInteractionEvent context) {

    }

    private void addRole(SlashCommandInteractionEvent context) {
        roles.computeIfAbsent(context.getUser(), k -> new HashSet<>());
        Role role = context.getOption("role").getAsRole();
        int botPermissionLevel = 0;
        for (Role r : KawaiiSan.getInstance().getAssignedRoles(context.getGuild())) {
            botPermissionLevel = Math.max(botPermissionLevel, r.getPosition());
        }
        if (botPermissionLevel < role.getPosition()) {
            context.reply("Sorry, but I can't assign this role. I can only assign roles that are below me in the hierarchy. :sweat:").queue();
            return;
        }
        roles.get(context.getUser()).add(role);
        context.reply("Role added!").queue();
    }

    private void sendRoles(SlashCommandInteractionEvent context) {
        ReplyCallbackAction reply = context.reply("Click buttons below to add a role!");
        Set<Role> roleList = roles.get(context.getUser());
        if (roleList == null || roleList.isEmpty()) {
            context.reply("You didn't provide any roles to add. Use /selfrole add @Role to add roles.").queue();
            return;
        }
        for (Role role : roleList) {
            reply.addActionRow(Button.primary(role.getId(), role.getName()));
        }
        context.getJDA().addEventListener(new ListenerAdapter() {
            @Override
            public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
                if (event.getMessage().getAuthor().equals(KawaiiSan.getInstance().getUser())) {
                    Role role = event.getGuild().getRoleById(event.getComponentId());
                    if (role == null) {
                        return;
                    }
                    event.getGuild().addRoleToMember(event.getUser(), role).queue();
                    event.reply("Assigned the role!").queue();
                }
            }
        });
        reply.queue();
        roles.get(context.getUser()).clear();
    }
}
